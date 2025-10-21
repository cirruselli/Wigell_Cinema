package com.leander.cinema.repository;

import com.leander.cinema.entity.Movie;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    // Kontrollera om screening krockar med annan screening i samma rum (i createBooking)
    @Query("""
    SELECT s FROM Screening s
    WHERE s.room = :room
      AND s.startTime < :endTime
      AND s.endTime > :startTime
""")
    List<Screening> findByRoomAndTimeOverlap(
            @Param("room") Room room,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    List<Screening> findByMovieIdAndStartTimeBetween(
            Long movieId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );

    List<Screening> findByRoom(Room room);

    List<Screening> findByMovie(Movie movie);

}
