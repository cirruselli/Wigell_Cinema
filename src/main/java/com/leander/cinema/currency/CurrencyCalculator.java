package com.leander.cinema.currency;

import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Ticket;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CurrencyCalculator {

    public static BigDecimal calculateTicketPrice(Ticket ticket) {
        if (ticket.getScreening() != null) {
            // Biljett till en föreställning (filmvisning)
            return ticket.getScreening().getPriceSek().setScale(2, RoundingMode.HALF_UP);
        }

        // Biljett till bokning med talare eller film
        if (ticket.getBooking() != null) {
            Booking booking = ticket.getBooking();

            BigDecimal basePerGuest = booking.getRoom().getPriceSek()
                    .divide(BigDecimal.valueOf(booking.getNumberOfGuests()), 2, RoundingMode.HALF_UP);

            BigDecimal extra = BigDecimal.ZERO;

            if (booking.getMovie() != null) {
                extra = BigDecimal.valueOf(200);
            } else if (booking.getSpeakerName() != null && !booking.getSpeakerName().isBlank()) {
                extra = BigDecimal.valueOf(400);
            }

            return basePerGuest.add(extra).setScale(2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }
}
