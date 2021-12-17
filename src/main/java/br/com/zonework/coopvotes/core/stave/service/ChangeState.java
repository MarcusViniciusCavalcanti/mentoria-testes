package br.com.zonework.coopvotes.core.stave.service;

import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import br.com.zonework.coopvotes.core.stave.service.state.StaveState;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChangeState {

    private final BeanFactory beanFactory;

    public void processNextState(StaveEntity stave) {
        var state = beanFactory.getBean(stave.getState(), StaveState.class);
        state.nextState(stave);
    }

    public void processCancel(StaveEntity stave) {
        var state = beanFactory.getBean(stave.getState(), StaveState.class);
        state.cancel(stave);
    }
}
