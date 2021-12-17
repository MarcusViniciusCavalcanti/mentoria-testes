package br.com.zonework.coopvotes.core.stave.service.state;

import br.com.zonework.coopvotes.core.notification.model.MessageNotification;
import br.com.zonework.coopvotes.core.notification.service.SenderNotification;
import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import br.com.zonework.coopvotes.structure.exception.IllegalStateStaveException;
import java.time.LocalDateTime;
import java.time.ZoneId;

public sealed interface StaveState permits
        StaveCalculatingVotesState,
        StaveCancelState,
        StaveCreateState,
        StaveSessionVotesDoneState,
        StaveVotingInProgressState {

    String getName();

    default void nextState(StaveEntity stave) {
        throw new IllegalStateStaveException(stave.getState(), getName());
    }

    default void cancel(StaveEntity stave) {
        throw new IllegalStateStaveException(stave.getState(), StateName.CANCEL.name());
    }

    default void sendResultStaveSessionMessage(
        String actionName,
        StaveEntity stave,
        SenderNotification senderNotification) {
        var message = new MessageNotification(
            actionName,
            LocalDateTime.now(ZoneId.of("UTC")),
            stave.getId(),
            stave.getTheme(),
            stave.getDescription(),
            stave.getStartSessionVoting(),
            stave.getTimeToLeaveSession(),
            stave.getCreateAt(),
            stave.getTotalVotesYes(),
            stave.getTotalVotesNo(),
            stave.getTotalVoteInvalid()
        );
        senderNotification.sendNotification(message);
    }

    enum StateName {
        CREATE, VOTING_IN_PROGRESS, CANCEL, CALCULATING_VOTES, SESSION_VOTES_DONE
    }
}
