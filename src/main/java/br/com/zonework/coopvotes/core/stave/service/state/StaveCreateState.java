package br.com.zonework.coopvotes.core.stave.service.state;

import static java.util.Objects.requireNonNullElse;
import static java.util.concurrent.TimeUnit.SECONDS;

import br.com.zonework.coopvotes.core.notification.service.SenderNotification;
import br.com.zonework.coopvotes.core.session.service.SchedulerExecutor;
import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("CREATE")
@RequiredArgsConstructor
public final class StaveCreateState implements StaveState {

    private final SenderNotification senderNotification;
    private final SchedulerExecutor schedulerExecutor;

    @Override
    public void nextState(StaveEntity stave) {
        log.info("Change State to stave");
        var ttl = SECONDS.toMillis(requireNonNullElse(stave.getTimeToLeaveSession(), 0L));
        schedulerExecutor.includeProcessEndSession(stave.getId(), ttl);
        stave.setState(StateName.VOTING_IN_PROGRESS.name());
    }

    @Override
    public void cancel(StaveEntity stave) {
        stave.setState(StateName.CANCEL.name());
        sendResultStaveSessionMessage(StateName.CANCEL.name(), stave, senderNotification);
    }

    @Override
    public String getName() {
        return StateName.CREATE.name();
    }
}
