package com.leander.cinema.repository;

import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomId(Long id);


    @Query("""
    SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
    FROM Booking b
    WHERE b.room = :room
      AND b.reservationStartTime < :newEnd
      AND b.reservationEndTime > :newStart
""")
    boolean overlaps(@Param("room") Room room,
                     @Param("newStart") LocalDateTime newStart,
                     @Param("newEnd") LocalDateTime newEnd);


    @Query("""
    SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END
    FROM Booking b
    WHERE b.room = :room
      AND b.id <> :bookingId
      AND b.reservationStartTime < :newEnd
      AND b.reservationEndTime > :newStart
""")
    boolean overlapsForUpdate(@Param("room") Room room,
                              @Param("newStart") LocalDateTime newStart,
                              @Param("newEnd") LocalDateTime newEnd,
                              @Param("bookingId") Long bookingId);

    List<Booking> findByCustomerId(Long customerId);


    @Query("""
    SELECT b FROM Booking b
    WHERE b.room = :room
    AND b.reservationStartTime < :endTime
    AND b.reservationEndTime > :startTime
""")
    List<Booking> findByRoomAndTimeOverlap(
            @Param("room") Room room,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
