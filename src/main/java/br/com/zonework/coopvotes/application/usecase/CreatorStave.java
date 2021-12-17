package br.com.zonework.coopvotes.application.usecase;

import static br.com.zonework.coopvotes.structure.data.MessageMapper.THEME_CONFLICT;
import static java.lang.Boolean.TRUE;
import static org.springframework.http.HttpStatus.CONFLICT;

import br.com.zonework.coopvotes.application.dto.InputNewStaveDto;
import br.com.zonework.coopvotes.application.dto.StaveDto;
import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import br.com.zonework.coopvotes.core.stave.service.state.StaveState.StateName;
import br.com.zonework.coopvotes.structure.exception.BusinessException;
import br.com.zonework.coopvotes.structure.repository.StaveRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatorStave {

    private final ModelMapper modelMapper;
    private final StaveRepository staveRepository;

    public StaveDto create(InputNewStaveDto input) {
        log.info("Running created new stave");
        var hasTheme = staveRepository.existsByThemeAndStateNot(
            input.getTheme(),
            StateName.SESSION_VOTES_DONE.name());
        if (TRUE.equals(hasTheme)) {
            throw new BusinessException(CONFLICT, THEME_CONFLICT.getCode());
        }
        var stave = modelMapper.map(input, StaveEntity.class);
        stave = staveRepository.saveAndFlush(stave);
        return modelMapper.map(stave, StaveDto.class);
    }
}
