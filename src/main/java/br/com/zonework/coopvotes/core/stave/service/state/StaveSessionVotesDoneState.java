package br.com.zonework.coopvotes.core.stave.service.state;

import org.springframework.stereotype.Component;

@Component("SESSION_VOTES_DONE")
public final class StaveSessionVotesDoneState implements StaveState {

    @Override
    public String getName() {
        return StateName.SESSION_VOTES_DONE.name();
    }
}
