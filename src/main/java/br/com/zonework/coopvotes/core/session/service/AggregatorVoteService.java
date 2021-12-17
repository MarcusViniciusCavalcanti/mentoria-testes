package br.com.zonework.coopvotes.core.session.service;

import static br.com.zonework.coopvotes.core.stave.service.state.StaveState.StateName.VOTING_IN_PROGRESS;

import br.com.zonework.coopvotes.core.session.entity.VoteEntity;
import br.com.zonework.coopvotes.core.session.model.ReprocessGrandAuthorizeAssociateMessage;
import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import br.com.zonework.coopvotes.structure.repository.StaveRepository;
import br.com.zonework.coopvotes.structure.repository.VoteRepository;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorVoteService {

    private final StaveRepository staveRepository;
    private final VoteRepository voteRepository;
    private final SchedulerExecutor schedulerExecutor;

    @Transactional
    public void includeVote(Long id, VoteEntity vote) {
        staveRepository.findById(id)
            .filter(stave -> stave.getState().equals(VOTING_IN_PROGRESS.name()))
            .map(stave -> {
                vote.setStave(stave);
                return stave;
            })
            .ifPresentOrElse(includeVote(vote), processError(id));
    }

    private Consumer<StaveEntity> includeVote(VoteEntity vote) {
        return stave -> {
            if (vote.getStatusAssociate().equals("ABLE_TO_VOTE_CB")) {
                log.debug("Schedule reprocess grand authorize vote associate");
                var message = new ReprocessGrandAuthorizeAssociateMessage(
                    vote.getStave().getId(),
                    vote.getAssociatedIdentifier()
                );
                schedulerExecutor.includeReprocessGrandAuthorizeAssociate(message);
            }

            log.debug("Save new vote to stave by id {}", stave.getId());
            voteRepository.saveAndFlush(vote);
        };
    }

    private Runnable processError(Long id) {
        return () ->
            log.info("Receive vote to stave id {} not found or session not initialized.", id);
    }
}
