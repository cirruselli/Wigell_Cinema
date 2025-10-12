package com.leander.cinema.service;

import com.leander.cinema.dto.CustomerDto.bookingDto.BookingPostRequestDto;
import com.leander.cinema.dto.CustomerDto.bookingDto.BookingResponseDto;
import com.leander.cinema.entity.Booking;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.exception.BookingCapacityExceededException;
import com.leander.cinema.mapper.BookingMapper;
import com.leander.cinema.repository.BookingRepository;
import com.leander.cinema.repository.RoomRepository;
import com.leander.cinema.repository.ScreeningRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final ScreeningRepository screeningRepository;

    public BookingService(BookingRepository bookingRepository, RoomRepository roomRepository, ScreeningRepository screeningRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.screeningRepository = screeningRepository;
    }

    //KONTROLLERA CREATEBOOKING-METODEN när föreställning och rum finns!!!!
    //----------------------------------------------------------
    public BookingResponseDto createBooking(BookingPostRequestDto body) {
        Booking booking = BookingMapper.toBookingEntity(body);

        Room room = roomRepository.findById(body.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Rummet med id " + body.roomId() + " hittades inte"));

        if (body.numberOfGuests() > room.getMaxGuests()) {
            throw new BookingCapacityExceededException("Antal gäster överstiger rummets kapacitet på " + room.getMaxGuests() + " gäster");
        }

        Screening screening = screeningRepository.findById(body.screeningId())
                .orElseThrow(() -> new EntityNotFoundException("Föreställningen med id " + body.screeningId() + " hittades inte"));

        // Eventuellt mappa booking, room och screening till respektive mapperDTO?

        //Beräkna totalpris -> komplettera med priser i klasserna för rum och föreställning



        booking.setRoom(room);
        booking.setScreening(screening);
        bookingRepository.save(booking);


        return BookingMapper.toBookingResponseDto(booking);

        /* Ska man kunna skapa en egen föreställning direkt vid bokningen? isf ändra i BookingPostRequestDto
        från ScreeningId till hela objektet Screening.
         */

            /*Ska man kunna boka samma rum och föreställning två gånger samma tid?? Om inte så inför
            nya attribut i Bookingklassen med starttid och sluttid för att kunna kontrollera om
            ett rum / föreställning är upptagen denna tid... Kolla om det finns en bokning för samma
            screening och room. Om flera bokningar kan kopplas till samma Screening och samma Room samtidigt,
            kan två bokningar boka samma rum samtidigt.
        */

        // Måste koppla ihop bokningen på den inloggade customern!
//        booking.setCustomer();
    }

}
