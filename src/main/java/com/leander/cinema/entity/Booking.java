package com.leander.cinema.entity;

import com.leander.cinema.service.BookingStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reservation_start_time", nullable = false)
    private LocalDateTime reservationStartTime;

    @Column(name = "reservation_end_time", nullable = false)
    private LocalDateTime reservationEndTime;

    @Column(name = "speaker_name")
    private String speakerName;

    @Column(name = "number_of_guests", nullable = false)
    private int numberOfGuests;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "room_equipments", joinColumns = @JoinColumn(name = "booking_id"))
    @Column(name = "equipment")
    private List<String> roomEquipment = new ArrayList<>();

    @Column(name = "total_price_SEK", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceSek;

    @Column(name = "total_price_USD", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPriceUsd;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    // ----- Snapshot av filmdata som sätts vid borttagning av film -----
    @Column(name = "movie_title")
    private String movieTitle;

    @Column(name = "movie_genre")
    private String movieGenre;

    @Column(name = "movie_age_limit")
    private Integer movieAgeLimit;

    @Column(name = "movie_duration")
    private Integer movieDuration;

    // -------------------------------

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;

    //RELATION
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Ticket> tickets = new ArrayList<>();


    public Booking() {
    }

    public Booking(LocalDateTime reservationStartTime, LocalDateTime reservationEndTime, String speakerName, Movie movie, int numberOfGuests, List<String> roomEquipment, BigDecimal totalPriceSek, BigDecimal totalPriceUsd, BookingStatus status, Room room, Customer customer) {
        this.reservationStartTime = reservationStartTime;
        this.reservationEndTime = reservationEndTime;
        this.speakerName = speakerName;
        this.movie = movie;
        this.numberOfGuests = numberOfGuests;
        this.roomEquipment = roomEquipment;
        this.totalPriceSek = totalPriceSek;
        this.totalPriceUsd = totalPriceUsd;
        this.status = status;
        this.room = room;
        this.customer = customer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getReservationStartTime() {
        return reservationStartTime;
    }

    public void setReservationStartTime(LocalDateTime reservationStartTime) {
        this.reservationStartTime = reservationStartTime;
    }

    public LocalDateTime getReservationEndTime() {
        return reservationEndTime;
    }

    public void setReservationEndTime(LocalDateTime reservationEndTime) {
        this.reservationEndTime = reservationEndTime;
    }

    public String getSpeakerName() {
        return speakerName;
    }

    public void setSpeakerName(String speakerName) {
        this.speakerName = speakerName;
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public List<String> getRoomEquipment() {
        return roomEquipment;
    }

    public void setRoomEquipment(List<String> bookingEquipment) {
        this.roomEquipment = bookingEquipment;
    }

    public BigDecimal getTotalPriceSek() {
        return totalPriceSek;
    }

    public void setTotalPriceSek(BigDecimal totalPriceSek) {
        this.totalPriceSek = totalPriceSek;
    }

    public BigDecimal getTotalPriceUsd() {
        return totalPriceUsd;
    }

    public void setTotalPriceUsd(BigDecimal totalPriceUsd) {
        this.totalPriceUsd = totalPriceUsd;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMovieGenre() {
        return movieGenre;
    }

    public void setMovieGenre(String movieGenre) {
        this.movieGenre = movieGenre;
    }

    public int getMovieAgeLimit() {
        return movieAgeLimit;
    }

    public void setMovieAgeLimit(int movieAgeLimit) {
        this.movieAgeLimit = movieAgeLimit;
    }

    public int getMovieDuration() {
        return movieDuration;
    }

    public void setMovieDuration(int movieDuration) {
        this.movieDuration = movieDuration;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
