package com.leander.cinema.repository;

import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;

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
}
