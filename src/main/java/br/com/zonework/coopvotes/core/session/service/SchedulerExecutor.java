package br.com.zonework.coopvotes.core.session.service;

import br.com.zonework.coopvotes.core.session.model.CalculateVoteMessage;
import br.com.zonework.coopvotes.core.session.model.CompletePartialCalculateVoteMessage;
import br.com.zonework.coopvotes.core.session.model.ReprocessGrandAuthorizeAssociateMessage;
import com.github.sonus21.rqueue.core.RqueueMessageEnqueuer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerExecutor {

    public static final String END_SESSION_VOTE = "end-session-vote";
    public static final String REPROCESS_GRAND_AUTHORIZE_ASSOCIATE =
        "reprocess-grand-authorize-associate";
    public static final String CALCULATE_VOTE = "calculate-vote";

    public static final String COMPLETE_PARTIAL_CALC_VOTE = "complete-partial-vote-cal-vote";

    private static final Integer TIME_REPROCESS_GRAND_AUTHORIZE = 10000;

    private final RqueueMessageEnqueuer rqueueMessageEnqueuer;

    public void includeProcessEndSession(Long id, Long time) {
        log.info("Scheduler send message in {} millis to topic {}", time, END_SESSION_VOTE);
        rqueueMessageEnqueuer.enqueueIn(END_SESSION_VOTE, id, time);
    }

    public void includeReprocessGrandAuthorizeAssociate(
        ReprocessGrandAuthorizeAssociateMessage message) {
        log.info(
            "Scheduler send message in {} millis to topic {}",
            TIME_REPROCESS_GRAND_AUTHORIZE,
            REPROCESS_GRAND_AUTHORIZE_ASSOCIATE
        );
        rqueueMessageEnqueuer.enqueueIn(
            REPROCESS_GRAND_AUTHORIZE_ASSOCIATE,
            message,
            TIME_REPROCESS_GRAND_AUTHORIZE
        );
    }

    public void includePageProcessVote(CalculateVoteMessage message) {
        log.info(
            "Send message immediately to topic {}",
            CALCULATE_VOTE
        );
        rqueueMessageEnqueuer.enqueue(CALCULATE_VOTE, message);
    }

    public void includePageProcessVote(
        CalculateVoteMessage message,
        Long delay) {
        log.info(
            "Send message with delay {} to topic {}",
            CALCULATE_VOTE,
            delay
        );

        rqueueMessageEnqueuer.enqueueIn(CALCULATE_VOTE, message, delay);
    }

    public void sendCompletePartialProcessCalculateVote(
        CompletePartialCalculateVoteMessage message) {
        log.info(
            "Send message immediately to topic {}",
            COMPLETE_PARTIAL_CALC_VOTE
        );

        rqueueMessageEnqueuer.enqueue(COMPLETE_PARTIAL_CALC_VOTE, message);
    }
}
