package br.com.zonework.coopvotes.core.stave.service.state;

import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("CANCEL")
public final class StaveCancelState implements StaveState {

    @Override
    public void nextState(StaveEntity stave) {
        log.info("cancel stave by id {}", stave.getId());
    }

    @Override
    public void cancel(StaveEntity stave) {
        log.info("cancel stave by id {}", stave.getId());
    }

    @Override
    public String getName() {
        return StateName.CANCEL.name();
    }
}
