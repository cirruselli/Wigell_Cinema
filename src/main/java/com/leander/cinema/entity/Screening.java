package com.leander.cinema.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "screenings")
public class Screening {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate startDate;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "room_id", nullable = false)
    private Room room;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "movie_id")
    private Movie movie;

    @Column(name = "custom_event")
    private String customEvent;
}
