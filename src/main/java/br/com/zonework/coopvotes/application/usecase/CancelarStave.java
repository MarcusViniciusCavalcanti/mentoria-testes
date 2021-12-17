package br.com.zonework.coopvotes.application.usecase;

import static br.com.zonework.coopvotes.structure.data.MessageMapper.*;
import static org.springframework.http.HttpStatus.*;

import br.com.zonework.coopvotes.core.stave.service.ChangeState;
import br.com.zonework.coopvotes.structure.data.MessageMapper;
import br.com.zonework.coopvotes.structure.exception.BusinessException;
import br.com.zonework.coopvotes.structure.exception.IllegalStateStaveException;
import br.com.zonework.coopvotes.structure.repository.StaveRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CancelarStave {

    private final ChangeState changeState;
    private final StaveRepository staveRepository;

    @Transactional
    public void cancelStave(Long id) {
        var stave = staveRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Stave by id %d".formatted(id)));

        try {
            changeState.processCancel(stave);
            staveRepository.saveAndFlush(stave);
        } catch (IllegalStateStaveException exception) {
            log.error("error in cancel stave {}", exception.getMessage(), exception);
            throw new BusinessException(UNPROCESSABLE_ENTITY, STAVE_CANCEL_ERROR.getCode());
        }
    }
}
