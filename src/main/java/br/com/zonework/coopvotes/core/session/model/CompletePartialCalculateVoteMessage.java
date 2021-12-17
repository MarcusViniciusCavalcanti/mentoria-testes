package br.com.zonework.coopvotes.core.session.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record CompletePartialCalculateVoteMessage(
    @JsonProperty("staveId") Long staveId,
    @JsonProperty("startDate") String startDate,
    @JsonProperty("endDate") String endDate
) {

    public LocalDateTime getStartDate() {
        return LocalDateTime.parse(startDate);
    }

    public LocalDateTime getEndDate() {
        return LocalDateTime.parse(endDate);
    }
}
