package com.leander.cinema.repository;

import com.leander.cinema.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {
    //Kontrollera om en screening krockar med någon annan screening i samma rum.
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


    //Kontrollera om samma film eller talare körs parallellt i olika rum
    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
        FROM Screening s
        WHERE 
            ((:movieId IS NOT NULL AND s.movie.id = :movieId)
            OR (:speakerName IS NOT NULL AND s.speakerName = :speakerName))
          AND (s.startTime < :endTime AND s.endTime > :startTime)
    """)
    boolean existsByMovieOrSpeakerAndOverlap(
            @Param("movieId") Long movieId,
            @Param("speakerName") String speakerName,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    /* Kontrollerar om en annan screening redan pågår i samma rum under angiven tid,
     exkluderar den aktuella screeningen.*/
    @Query("""
    SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
    FROM Screening s
    WHERE s.room.id = :roomId
      AND s.id <> :screeningId
      AND (s.startTime < :reservationEnd AND s.endTime > :reservationStart)
""")
    boolean existsScreeningInRoomDuring(
            @Param("roomId") Long roomId,
            @Param("reservationStart") LocalDateTime reservationStart,
            @Param("reservationEnd") LocalDateTime reservationEnd,
            @Param("screeningId") Long screeningId
    );


 /* Kontrollerar om samma film redan visas parallellt i ett annat rum.
 * Ignorerar den aktuella screeningen själv. */
    @Query("""
    SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
    FROM Screening s
    WHERE s.movie.id = (
        SELECT sc.movie.id FROM Screening sc WHERE sc.id = :screeningId
    )
    AND s.id <> :screeningId
    AND (s.startTime < :endTime AND s.endTime > :startTime)
""")
    boolean existsByMovieIdAndTimeOverlap(
            @Param("screeningId") Long screeningId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
