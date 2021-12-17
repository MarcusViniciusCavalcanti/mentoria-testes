package br.com.zonework.coopvotes.core.notification.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record MessageNotification(
    @JsonProperty("action") String action,
    @JsonProperty("dateAction") LocalDateTime date,
    @JsonProperty("staveId") Long staveId,
    @JsonProperty("theme") String theme,
    @JsonProperty("description") String description,
    @JsonProperty("startSessionVoting") LocalDateTime startSessionVoting,
    @JsonProperty("timeToLeaveSession") Long timeToLeaveSession,
    @JsonProperty("dateCreationStave") LocalDateTime createAt,
    @JsonProperty("totalVotesYes") Integer totalVotesYes,
    @JsonProperty("totalVotesNo") Integer totalVotesNo,
    @JsonProperty("totalVotesInvalid") Integer totalVotesInvalid) {
}
