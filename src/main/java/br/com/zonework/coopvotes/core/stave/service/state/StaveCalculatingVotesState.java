package br.com.zonework.coopvotes.core.stave.service.state;

import static br.com.zonework.coopvotes.core.stave.service.state.StaveState.StateName.CALCULATING_VOTES;
import static br.com.zonework.coopvotes.core.stave.service.state.StaveState.StateName.SESSION_VOTES_DONE;

import br.com.zonework.coopvotes.core.notification.service.SenderNotification;
import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import br.com.zonework.coopvotes.structure.repository.StaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("CALCULATING_VOTES")
public final class StaveCalculatingVotesState implements StaveState {

    private final SenderNotification senderNotification;
    private final StaveRepository staveRepository;

    @Override
    public void nextState(StaveEntity stave) {
        stave.setState(SESSION_VOTES_DONE.name());
        staveRepository.saveAndFlush(stave);
        sendResultStaveSessionMessage(SESSION_VOTES_DONE.name(), stave, senderNotification);
    }

    @Override
    public String getName() {
        return CALCULATING_VOTES.name();
    }
}
