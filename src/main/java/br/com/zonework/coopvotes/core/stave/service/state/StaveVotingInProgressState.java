package br.com.zonework.coopvotes.core.stave.service.state;

import br.com.zonework.coopvotes.core.notification.service.SenderNotification;
import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("VOTING_IN_PROGRESS")
@RequiredArgsConstructor
public final class StaveVotingInProgressState implements StaveState {

    private final SenderNotification senderNotification;

    @Override
    public void nextState(StaveEntity stave) {
        stave.setState(StateName.CALCULATING_VOTES.name());
    }

    @Override
    public void cancel(StaveEntity stave) {
        stave.setState(StateName.CANCEL.name());
        sendResultStaveSessionMessage(StateName.CANCEL.name(), stave, senderNotification);
    }

    @Override
    public String getName() {
        return StateName.VOTING_IN_PROGRESS.name();
    }
}
