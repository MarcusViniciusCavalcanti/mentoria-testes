package br.com.zonework.coopvotes.core.session.service;

import static java.lang.Boolean.FALSE;
import static java.lang.Math.floorDiv;
import static java.lang.Math.toIntExact;
import static java.util.concurrent.CompletableFuture.runAsync;

import br.com.zonework.coopvotes.core.session.model.CalculateVoteMessage;
import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import br.com.zonework.coopvotes.structure.repository.VoteRepository;
import java.util.stream.LongStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExecutorCalculatorVote {

    private static final Long DIVIDER = 1000L;
    private static final long PAGE_NUMBER_FIRST = 0L;
    private static final int START_INCLUSIVE = 0;

    private final VoteRepository voteRepository;
    private final SchedulerExecutor schedulerExecutor;
    private final ProcessVoteListener processVoteListener;
    private final FinisherCalculateVoteListener finisherCalculateVoteListener;

    @Async
    public void initialize(StaveEntity stave) {
        runAsync(() -> {
            var start = stave.getStartSessionVoting();
            var end = stave.getEndSessionVoting();
            var id = stave.getId();

            var amount = voteRepository
                .countByStaveIdAndDateBetweenAndProcessed(id, start, end, FALSE);

            log.info(
                "Running sum votes stave by id {} and between date: {}, {}, total votes {}",
                id,
                start,
                end,
                amount);
            if (amount <= 0) {
                log.warn("Skip initialize calculate votes, because 0 votes found in database.");
                finisherCalculateVoteListener.finisherSessionVote(id);
                return;
            }

            if ((amount / DIVIDER) <= 1) {
                processVoteListener.executeCalcule(
                    stave.getId(),
                    PAGE_NUMBER_FIRST,
                    start,
                    end,
                    toIntExact(amount),
                    Boolean.TRUE
                );
            } else {
                var amountPage = floorDiv(amount, DIVIDER) + 1;
                log.info("Separe process sum votes in {}", amountPage);
                LongStream.rangeClosed(START_INCLUSIVE, amountPage).forEach(page -> {
                    var startDate = start.toString();
                    var endDate = end.toString();
                    var message = new CalculateVoteMessage(id, page, startDate, endDate);
                    schedulerExecutor.includePageProcessVote(message);
                });
            }
        });
    }
}
