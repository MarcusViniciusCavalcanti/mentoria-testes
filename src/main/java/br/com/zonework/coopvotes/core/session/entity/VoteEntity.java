package br.com.zonework.coopvotes.core.session.entity;

import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vote")
@Data
public class VoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vote_type")
    private Boolean voteType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_stave", nullable = false)
    private StaveEntity stave;

    @Column(name = "associated_identifier")
    private String associatedIdentifier;

    @Column(name = "associate_status")
    private String statusAssociate;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "processed")
    private Boolean processed;
}
