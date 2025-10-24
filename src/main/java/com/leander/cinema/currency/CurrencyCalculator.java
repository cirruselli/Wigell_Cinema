package com.leander.cinema.currency;

import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.entity.Ticket;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CurrencyCalculator {

    //Hjälpmetod för att beräkna biljettpris
    public static BigDecimal calculateTicketPrice(Ticket ticket) {
        if (ticket.getBooking() != null) {
            Booking booking = ticket.getBooking();
            if (booking.getSpeakerName() != null && !booking.getSpeakerName().isBlank()) {
                BigDecimal pricePerGuest = booking.getTotalPriceSek()
                        .divide(BigDecimal.valueOf(booking.getNumberOfGuests()), 2, RoundingMode.HALF_UP);

                // Lägg till 200 kr extra per biljett
                return pricePerGuest.add(BigDecimal.valueOf(400));
            }
            if (booking.getMovie() != null) {
                BigDecimal roomPricePerGuest = booking.getRoom().getPriceSek()
                        .divide(BigDecimal.valueOf(booking.getNumberOfGuests()), 2, RoundingMode.HALF_UP);

                return roomPricePerGuest.add(BigDecimal.valueOf(200));
            }
        }
        if (ticket.getScreening() != null) {
            Screening screening = ticket.getScreening();
            BigDecimal roomPricePerGuest = screening.getPriceSek();
            return roomPricePerGuest;
        }
        return BigDecimal.ZERO;
    }
}
