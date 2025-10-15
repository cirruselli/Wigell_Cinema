package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningRequestDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.entity.Movie;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.entity.Speaker;
import com.leander.cinema.exception.InvalidScreeningException;
import com.leander.cinema.mapper.ScreeningMapper;
import com.leander.cinema.repository.MovieRepository;
import com.leander.cinema.repository.RoomRepository;
import com.leander.cinema.repository.ScreeningRepository;
import com.leander.cinema.repository.SpeakerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final SpeakerRepository speakerRepository;
    private final MovieRepository movieRepository;

    public ScreeningService(ScreeningRepository screeningRepository,
                            RoomRepository roomRepository, SpeakerRepository speakerRepository, MovieRepository movieRepository) {
        this.screeningRepository = screeningRepository;
        this.roomRepository = roomRepository;
        this.speakerRepository = speakerRepository;
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminScreeningResponseDto> getAllScreenings() {
        List<Screening> screenings = screeningRepository.findAll();

        List<AdminScreeningResponseDto> responseList = new ArrayList<>();
        for (Screening screening : screenings) {
            AdminScreeningResponseDto screeningResponse = ScreeningMapper.toAdminScreeningResponseDto(screening);
            responseList.add(screeningResponse);
        }
        return responseList;
    }

    @Transactional
    public AdminScreeningResponseDto createScreening(AdminScreeningRequestDto body) {

        Room room = roomRepository.findById(body.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Rummet med id " + body.roomId() + " hittades inte"));

        if ((body.speakerId() == null && body.movieId() == null) ||
                (body.speakerId() != null && body.movieId() != null)) {
            throw new InvalidScreeningException("En screening måste ha antingen talare eller film, inte båda.");
        }

        Speaker speaker = null;
        Movie movie = null;
        if (body.speakerId() != null) {
            speaker = speakerRepository.findById(body.speakerId())
                    .orElseThrow(() -> new EntityNotFoundException("Talare med id " + body.speakerId() + " hittades inte"));
        } else {
            movie = movieRepository.findById(body.movieId())
                    .orElseThrow(() -> new EntityNotFoundException("Film med id " + body.movieId() + " hittades inte"));
        }

        boolean conflict = screeningRepository.existsOverlappingScreening(
                body.roomId(),
                body.startTime(),
                body.endTime()
        );

        if (conflict) {
            throw new InvalidScreeningException("Rummet är redan bokat under denna tid.");
        }

        Screening screening = ScreeningMapper.toScreeningEntity(body);
        screening.setRoom(room);
        screening.setSpeaker(speaker);
        screening.setMovie(movie);

        BigDecimal factor = new BigDecimal("0.11");
        screening.setPriceUsd(screening.getPriceSek().multiply(factor));

        screeningRepository.save(screening);

        return ScreeningMapper.toAdminScreeningResponseDto(screening);
    }
}
