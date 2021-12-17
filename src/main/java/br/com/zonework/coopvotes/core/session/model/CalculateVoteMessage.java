package br.com.zonework.coopvotes.core.session.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record CalculateVoteMessage(
    @JsonProperty("staveId") Long staveId,
    @JsonProperty("pageNumber") Long pageNumber,
    @JsonProperty("startDate") String startDate,
    @JsonProperty("endDate") String endDate
) {

    public LocalDateTime startDateParseLocalDate() {
        return LocalDateTime.parse(startDate);
    }

    public LocalDateTime endDateParseLocalDate() {
        return LocalDateTime.parse(endDate);
    }
}
