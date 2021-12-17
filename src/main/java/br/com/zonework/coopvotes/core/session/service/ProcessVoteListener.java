package br.com.zonework.coopvotes.core.session.service;

import static br.com.zonework.coopvotes.core.session.service.SchedulerExecutor.CALCULATE_VOTE;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.lang.Math.toIntExact;

import br.com.zonework.coopvotes.core.session.entity.VoteEntity;
import br.com.zonework.coopvotes.core.session.model.CalculateVoteMessage;
import br.com.zonework.coopvotes.core.session.model.CompletePartialCalculateVoteMessage;
import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import br.com.zonework.coopvotes.structure.repository.StaveRepository;
import br.com.zonework.coopvotes.structure.repository.VoteRepository;
import com.github.sonus21.rqueue.annotation.RqueueListener;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessVoteListener {

    private static final int SIZE = 1000;
    private final VoteRepository voteRepository;
    private final StaveRepository staveRepository;
    private final SchedulerExecutor schedulerExecutor;
    private final FinisherCalculateVoteListener finisherCalculateVoteListener;

    @Transactional
    @RqueueListener(value = CALCULATE_VOTE, numRetries = "3")
    public void processVote(CalculateVoteMessage message) {
        log.info("Receive message to topic {} payload {}", CALCULATE_VOTE, message);
        var pageNumber = message.pageNumber();
        var start = message.startDateParseLocalDate();
        var end = message.endDateParseLocalDate();
        var staveId = message.staveId();

        executeCalcule(staveId, pageNumber, start, end, SIZE, FALSE);
    }

    @Transactional
    public void executeCalcule(
        Long staveId,
        Long pageNumber,
        LocalDateTime start,
        LocalDateTime end,
        Integer size,
        Boolean oneIteration) {
        log.info("Start process calculate vote size {}", size);
        var spec = getSpec(staveId, start, end);
        var pageRequest = PageRequest.of(toIntExact(pageNumber), size);
        var page = voteRepository.findAll(spec, pageRequest);
        var votes = page.getContent().stream()
            .map(setVoteProcessed())
            .toList();

        if (FALSE.equals(page.isEmpty())) {
            var stave = votes.get(0).getStave();
            voteRepository.saveAllAndFlush(votes);

            processVoteYes(votes, stave);
            processVoteNo(votes, stave);
            processInvalidVote(votes, stave);
            log.info("Process votes {} complete", votes.size());
        } else {
            checkLastPage(staveId, start, end);
        }

        if (TRUE.equals(oneIteration)) {
            finisherCalculateVoteListener.finisherSessionVote(staveId);
        }
    }

    private void checkLastPage(Long staveId, LocalDateTime start, LocalDateTime end) {
        log.info("Last page send message complete Partial process");
        var message = new CompletePartialCalculateVoteMessage(
            staveId,
            start.toString(),
            end.toString()
        );
        schedulerExecutor.sendCompletePartialProcessCalculateVote(message);
    }

    private void processInvalidVote(List<VoteEntity> votes, StaveEntity stave) {
        log.debug("Calculate votes invalid to Stave {}", stave.getId());
        var votesInvalid = votes.stream()
            .filter(vote -> vote.getStatusAssociate().equals("ABLE_TO_VOTE_CB")
                            || vote.getStatusAssociate().equals("INVALID")
                            || vote.getStatusAssociate().equals("UNABLE_TO_VOTE"))
            .toList();

        staveRepository.incrementVoteInvalid(stave.getId(), votesInvalid.size());
    }

    private void processVoteNo(List<VoteEntity> votes, StaveEntity stave) {
        log.debug("Calculate votes no to Stave {}", stave.getId());
        var votesNo = votes.stream()
            .filter(vote -> vote.getStatusAssociate().equals("ABLE_TO_VOTE"))
            .filter(vote -> FALSE.equals(vote.getVoteType()))
            .toList();

        staveRepository.incrementVoteNo(stave.getId(), votesNo.size());
    }

    private void processVoteYes(List<VoteEntity> votes, StaveEntity stave) {
        log.debug("Calculate votes yes to Stave {}", stave.getId());
        var votesYes = votes.stream()
            .filter(vote -> vote.getStatusAssociate().equals("ABLE_TO_VOTE"))
            .filter(vote -> TRUE.equals(vote.getVoteType()))
            .toList();

        staveRepository.incrementVoteYes(stave.getId(), votesYes.size());
    }

    private Function<VoteEntity, VoteEntity> setVoteProcessed() {
        return vote -> {
            vote.setProcessed(TRUE);
            return vote;
        };
    }

    private Specification<VoteEntity> getSpec(
        Long staveId,
        LocalDateTime start,
        LocalDateTime end) {
        log.debug("Build specification to params: id {}, start {}, end {}", staveId, start, end);
        return (root, query, criteriaBuilder) -> {
            query.distinct(TRUE);
            return Specification.where(filterDate(start, end))
                .and(filterId(staveId))
                .and(filterNotProcess())
                .toPredicate(root, query, criteriaBuilder);
        };
    }

    private Specification<VoteEntity> filterDate(
        LocalDateTime start,
        LocalDateTime end) {
        log.debug("Build specification to params end {}, start {}", start, end);
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.between(root.get("date"), start, end);
    }

    private Specification<VoteEntity> filterId(Long staveId) {
        log.debug("Build specification to params: StaveId {}", staveId);
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("stave").get("id"), staveId);
    }

    private Specification<VoteEntity> filterNotProcess() {
        log.debug("Build specification to params: processed FALSE");
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.equal(root.get("processed"), FALSE);
    }
}
