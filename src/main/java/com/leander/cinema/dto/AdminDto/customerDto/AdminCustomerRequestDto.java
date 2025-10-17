//package com.leander.cinema.dto.AdminDto.customerDto;
//
//import com.leander.cinema.dto.AdminDto.addressDto.AdminAddressRequestDto;
//import com.leander.cinema.dto.AdminDto.bookingDto.AdminBookingUpdateRequestDto;
//import com.leander.cinema.dto.AdminDto.ticketDto.AdminTicketUpdateRequestDto;
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//
//import java.util.List;
//
//public record AdminCustomerRequestDto(
//        @NotBlank(message = "Förnamn måste anges")
//        @Size(max = 50, message = "Teckenlängd max 50")
//        String firstName,
//        @NotBlank(message = "Efternamn måste anges")
//        @Size(max = 50, message = "Teckenlängd max 50")
//        String lastName,
//        @Email(message = "Ogiltig e-postadress")
//        @NotBlank(message = "Email måste anges")
//        @Size(max = 255, message = "Teckenlängd max 255")
//        String email,
//        @NotBlank(message = "Telefonnummer måste anges")
//        @Size(max = 10, message = "Teckenlängd max 10")
//        String phone,
//        @Valid //Validerar listan om det finns element i den
//            /*Size och notnull kompletterar varandra för att tvinga att ett element
//        finns i adresslistan vid både post och put.
//         */
//        @NotNull(message = "Minst en adress måste anges")
//        @Size(min = 1, message = "Minst en adress måste anges")
//        List<AdminAddressRequestDto> addresses,
//        @Valid // Validerar listan om det finns element i den
//        List<AdminTicketUpdateRequestDto> tickets,
//        @Valid // Validerar listan om det finns element i den
//        List<AdminBookingUpdateRequestDto> bookings
//) {
//}
