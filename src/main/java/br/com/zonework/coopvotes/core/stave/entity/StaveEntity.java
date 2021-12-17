package br.com.zonework.coopvotes.core.stave.entity;

import static java.util.Objects.requireNonNullElse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "stave")
public class StaveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "theme")
    private String theme;

    @Column(name = "description")
    private String description;

    @Column(name = "state")
    private String state;

    @Column(name = "start_session_voting")
    private LocalDateTime startSessionVoting;

    @Column(name = "time_to_leave_session")
    private Long timeToLeaveSession;

    @Column(name = "create_at")
    private LocalDateTime createAt;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "total_votes_yes")
    private int totalVotesYes;

    @Column(name = "total_votes_no")
    private int totalVotesNo;

    @Column(name = "total_votes_invalid")
    private int totalVoteInvalid;

    public Integer getTotalVotes() {
        return requireNonNullElse(totalVotesYes, 0)
               + requireNonNullElse(totalVotesNo, 0)
               + requireNonNullElse(totalVoteInvalid, 0);
    }

    public LocalDateTime getEndSessionVoting() {
        if (Objects.nonNull(timeToLeaveSession) && timeToLeaveSession > 0) {
            return startSessionVoting.plusSeconds(timeToLeaveSession);
        }

        return null;
    }

    @PrePersist
    void createAt() {
        createAt = LocalDateTime.now(ZoneId.of("UTC"));
        updateAt();
    }

    @PreUpdate
    void updateAt() {
        updateAt = LocalDateTime.now(ZoneId.of("UTC"));
    }
}
