package com.leander.cinema.repository;

import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    boolean existsByRoomAndReservationStartTimeLessThanAndReservationEndTimeGreaterThan(
            Room room, LocalDateTime reservationEndTime, LocalDateTime reservationStartTime);

}
