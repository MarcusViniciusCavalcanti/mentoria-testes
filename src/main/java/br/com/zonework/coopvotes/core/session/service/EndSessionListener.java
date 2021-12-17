package br.com.zonework.coopvotes.core.session.service;

import br.com.zonework.coopvotes.core.stave.service.ChangeState;
import br.com.zonework.coopvotes.structure.repository.StaveRepository;
import br.com.zonework.coopvotes.structure.repository.VoteRepository;
import com.github.sonus21.rqueue.annotation.RqueueListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndSessionListener {

    private final ExecutorCalculatorVote executorCalculatorVote;
    private final StaveRepository staveRepository;
    private final ChangeState changeState;

    @Transactional
    @RqueueListener(value = SchedulerExecutor.END_SESSION_VOTE)
    public void receiveEndSession(Long id) {
        log.info("Receive message -> {} from topic {}", id, SchedulerExecutor.END_SESSION_VOTE);
        staveRepository.findById(id)
            .ifPresentOrElse(stave -> {
                log.info("Running end session stave");
                changeState.processNextState(stave);
                executorCalculatorVote.initialize(stave);
            }, () -> log.warn("Receive Message end Session, but Stave by id {} not found", id));

    }
}
