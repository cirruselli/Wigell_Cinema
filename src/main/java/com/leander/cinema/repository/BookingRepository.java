package com.leander.cinema.repository;

import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByRoomAndReservationStartTimeLessThanAndReservationEndTimeGreaterThan(
            Room room, LocalDateTime reservationEndTime, LocalDateTime reservationStartTime);

    boolean existsByRoomAndReservationStartTimeLessThanAndReservationEndTimeGreaterThanAndIdNot(
            Room room,
            LocalDateTime reservationEndTime,
            LocalDateTime reservationStartTime,
            Long bookingId
    );

    List<Booking> findByRoomId(Long id);

    boolean existsByScreeningId(Long aLong);



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

}
