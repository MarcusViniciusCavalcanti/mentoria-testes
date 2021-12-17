package br.com.zonework.coopvotes.structure.repository;

import br.com.zonework.coopvotes.core.session.entity.VoteEntity;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<VoteEntity, Long>,
    JpaSpecificationExecutor<VoteEntity> {

    Optional<VoteEntity> findByStaveIdAndAssociatedIdentifier(Long staveId, String identifier);

    Boolean existsByAssociatedIdentifierAndStaveId(String identifier, Long staveId);

    Optional<VoteEntity> findByAssociatedIdentifier(String identifier);

    Long countByStaveIdAndDateBetweenAndProcessed(
        Long id,
        LocalDateTime start,
        LocalDateTime end,
        Boolean hasProcessed);

    void deleteAllByStaveId(Long id);
}
