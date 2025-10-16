package com.leander.cinema.service;

import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningRequestDto;
import com.leander.cinema.dto.AdminDto.screeningDto.AdminScreeningResponseDto;
import com.leander.cinema.entity.Movie;
import com.leander.cinema.entity.Room;
import com.leander.cinema.entity.Screening;
import com.leander.cinema.exception.InvalidScreeningException;
import com.leander.cinema.mapper.ScreeningMapper;
import com.leander.cinema.repository.MovieRepository;
import com.leander.cinema.repository.RoomRepository;
import com.leander.cinema.repository.ScreeningRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final RoomRepository roomRepository;
    private final MovieRepository movieRepository;

    public ScreeningService(ScreeningRepository screeningRepository,
                            RoomRepository roomRepository, MovieRepository movieRepository) {
        this.screeningRepository = screeningRepository;
        this.roomRepository = roomRepository;
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

        if ((body.speakerName() == null && body.movieId() == null) ||
                (body.speakerName() != null && body.movieId() != null)) {
            throw new InvalidScreeningException("En screening måste ha antingen talare eller film, inte båda.");
        }

        String speaker = null;
        Movie movie = null;

        if (body.speakerName() != null) {
            speaker = body.speakerName().trim();
        } else {
            movie = movieRepository.findById(body.movieId())
                    .orElseThrow(() -> new EntityNotFoundException("Film med id " + body.movieId() + " hittades inte"));
        }

        //Förhindrar att två visningar sker i samma salong samtidigt
        boolean conflict = screeningRepository.existsOverlappingScreening(
                body.roomId(),
                body.startTime(),
                body.endTime()
        );

        if (conflict) {
            throw new InvalidScreeningException("Rummet är redan bokat under denna tid.");
        }

        //Förhindrar att samma film eller talare schemaläggs parallellt
        boolean contentConflict = screeningRepository.existsByMovieOrSpeakerAndOverlap(
                body.movieId(),
                body.speakerName(),
                body.startTime(),
                body.endTime()
        );

        if (contentConflict) {
            throw new InvalidScreeningException("Denna film eller talare är redan schemalagd under den valda tiden.");
        }


        Screening screening = ScreeningMapper.toScreeningEntity(body);
        screening.setRoom(room);
        screening.setSpeakerName(speaker);
        screening.setMovie(movie);

        BigDecimal factor = new BigDecimal("0.11");
        screening.setPriceUsd(screening.getPriceSek().multiply(factor));

        screeningRepository.save(screening);

        return ScreeningMapper.toAdminScreeningResponseDto(screening);
    }

    @Transactional
    public boolean deleteScreening(Long id) {
        Optional<Screening> screening = screeningRepository.findById(id);

        if (screening.isPresent()) {
            screeningRepository.delete(screening.get());
            return true;
        }
        return false;
    }
}
