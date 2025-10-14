package com.leander.cinema.mapper;

import com.leander.cinema.dto.CustomerDto.ticketDto.TicketRequestDto;
import com.leander.cinema.entity.Ticket;

public class TicketMapper {


    public static Ticket toTicketEntity (TicketRequestDto body){
        return new Ticket(

        );
    }
}
