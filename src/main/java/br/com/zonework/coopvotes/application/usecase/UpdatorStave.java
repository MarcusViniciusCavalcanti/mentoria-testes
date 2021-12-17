package br.com.zonework.coopvotes.application.usecase;

import static br.com.zonework.coopvotes.structure.data.MessageMapper.THEME_CONFLICT;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.springframework.http.HttpStatus.CONFLICT;

import br.com.zonework.coopvotes.application.dto.InputUpdateStaveDto;
import br.com.zonework.coopvotes.application.dto.StaveDto;
import br.com.zonework.coopvotes.core.stave.service.state.StaveState.StateName;
import br.com.zonework.coopvotes.structure.exception.BusinessException;
import br.com.zonework.coopvotes.structure.repository.StaveRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdatorStave {

    private final ModelMapper modelMapper;
    private final StaveRepository staveRepository;

    @Transactional
    public StaveDto update(Long id, InputUpdateStaveDto input) {
        log.info("Running updated stave {}", id);
        return staveRepository.findById(id)
            .map(stave -> {
                if (FALSE.equals(stave.getTheme().equals(input.getTheme()))) {
                    var hasTheme = staveRepository.existsByThemeAndStateNot(
                        input.getTheme(),
                        StateName.SESSION_VOTES_DONE.name());
                    if (TRUE.equals(hasTheme)) {
                        throw new BusinessException(CONFLICT, THEME_CONFLICT.getCode());
                    }
                }

                stave.setTheme(input.getTheme());
                stave.setDescription(input.getDescription());

                return staveRepository.saveAndFlush(stave);
            })
            .map(stave -> modelMapper.map(stave, StaveDto.class))
            .orElseThrow(() -> new EntityNotFoundException("Stave by id %d".formatted(id)));
    }
}
