package br.com.zonework.coopvotes.application.usecase;

import static java.util.concurrent.CompletableFuture.runAsync;

import br.com.zonework.coopvotes.application.dto.InputStartSessionDto;
import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import br.com.zonework.coopvotes.core.stave.service.ChangeState;
import br.com.zonework.coopvotes.structure.repository.StaveRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartSessionVote {

    private final ChangeState changeState;
    private final StaveRepository staveRepository;

    @Transactional
    public void startSession(Long id, InputStartSessionDto input) {
        log.info("Running start session to stave by id {}", id);
        var stave = staveRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("id: %d".formatted(id)));
        prepareAndStartSession(stave, input);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void prepareAndStartSession(StaveEntity stave, InputStartSessionDto input) {
        runAsync(() -> {
            var start = LocalDateTime.now(ZoneId.of("UTC"));
            stave.setStartSessionVoting(start);
            stave.setTimeToLeaveSession(input.getTimeToLive());

            changeState.processNextState(stave);
            staveRepository.saveAndFlush(stave);
        });
    }
}
