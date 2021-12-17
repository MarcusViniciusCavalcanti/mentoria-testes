package br.com.zonework.coopvotes.structure.repository;

import br.com.zonework.coopvotes.core.stave.entity.StaveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StaveRepository extends JpaRepository<StaveEntity, Long> {

    Boolean existsByThemeAndStateNot(String theme, String state);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update StaveEntity set totalVotesYes = totalVotesYes + :value where id = :id")
    void incrementVoteYes(@Param("id") Long id, @Param("value") Integer value);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update StaveEntity set totalVotesNo = totalVotesNo + :value where id = :id")
    void incrementVoteNo(@Param("id") Long id, @Param("value") Integer value);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update StaveEntity set totalVoteInvalid = totalVoteInvalid + :value where id = :id")
    void incrementVoteInvalid(@Param("id") Long id, @Param("value") Integer value);
}
