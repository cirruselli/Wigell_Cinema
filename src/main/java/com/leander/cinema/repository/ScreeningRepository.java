package com.leander.cinema.repository;

import com.leander.cinema.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
    @Query("""
    SELECT COUNT(s) > 0
    FROM Screening s
    WHERE s.room.id = :roomId
      AND (:startTime < s.endTime AND :endTime > s.startTime)
""")
    boolean existsOverlappingScreening(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

}
