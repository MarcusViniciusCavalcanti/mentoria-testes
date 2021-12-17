package br.com.zonework.coopvotes.core.session.service;

import static br.com.zonework.coopvotes.core.stave.service.state.StaveState.StateName.CALCULATING_VOTES;
import static java.lang.Boolean.FALSE;
import static java.util.concurrent.TimeUnit.SECONDS;

import br.com.zonework.coopvotes.core.session.model.CalculateVoteMessage;
import br.com.zonework.coopvotes.core.session.model.CompletePartialCalculateVoteMessage;
import br.com.zonework.coopvotes.core.stave.service.ChangeState;
import br.com.zonework.coopvotes.structure.repository.StaveRepository;
import br.com.zonework.coopvotes.structure.repository.VoteRepository;
import com.github.sonus21.rqueue.annotation.RqueueListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinisherCalculateVoteListener {

    private final StaveRepository staveRepository;
    private final VoteRepository voteRepository;
    private final ChangeState changeState;
    private final SchedulerExecutor schedulerExecutor;

    @Transactional
    @RqueueListener(value = SchedulerExecutor.COMPLETE_PARTIAL_CALC_VOTE)
    public void exec(CompletePartialCalculateVoteMessage message) {
        log.info("Receive message to complete partial calculate votes");
        var amountAwaitProcess = voteRepository.countByStaveIdAndDateBetweenAndProcessed(
            message.staveId(),
            message.getStartDate(),
            message.getEndDate(),
            FALSE);

        log.info("Amount await process vote {}", amountAwaitProcess);
        if (amountAwaitProcess == 0) {
            finisherSessionVote(message.staveId());
            voteRepository.deleteAllByStaveId(message.staveId());
        } else {
            log.info("Retry to calculate votes");
            var retry = new CalculateVoteMessage(
                message.staveId(),
                0L,
                message.startDate(),
                message.endDate()
            );
            schedulerExecutor.includePageProcessVote(retry, SECONDS.toMillis(1));
        }
    }

    @Transactional
    public void finisherSessionVote(Long staveId) {
        log.info("Finisher process calculate votes");
        staveRepository.findById(staveId)
            .filter(stave -> stave.getState().equals(CALCULATING_VOTES.name()))
            .ifPresent(stave -> {
                log.info("change state Stave by id: {}", staveId);
                changeState.processNextState(stave);
            });
    }
}
