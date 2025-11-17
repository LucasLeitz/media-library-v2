package com.lucasleitz.medialibrary.controllers;

import com.lucasleitz.medialibrary.dtos.BookDetailsDto;
import com.lucasleitz.medialibrary.dtos.GameDetailsDto;
import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.services.MediaService;
import com.lucasleitz.medialibrary.enums.MediaType;
import com.lucasleitz.medialibrary.dtos.MediaDto;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService){
        this.mediaService = mediaService;
    }

    @PostMapping
    public ResponseEntity<MediaDto> create(@Valid @RequestBody MediaDto dto) {
        Media saved = mediaService.create(
                dto.getType(),
                dto.getImageUrl(),
                dto.getName(),
                dto.getStatus(),
                dto.getStartedAt(),
                dto.getCompletedAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(saved));
    }

    @GetMapping("/{id}")
    public MediaDto get(@PathVariable UUID id) {
        Media m = mediaService.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Media %s not found".formatted(id)));
        return toDto(m);
    }

    @GetMapping
    public List<MediaDto> list(
            @RequestParam(required = false, name = "q") String namePart,
            @RequestParam(required = false) MediaType type,
            @RequestParam(required = false) MediaStatus status
    ) {
        List<Media> results;

        if (namePart != null && !namePart.isBlank()) {
            results = mediaService.searchByName(namePart);
        } else if (type != null) {
            results = mediaService.listByType(type);
        } else if (status != null) {
            results = mediaService.listByStatus(status);
        } else {
            results = mediaService.searchByName("");
        }

        return results.stream().map(this::toDto).toList();
    }

    @PatchMapping("/{id}/name")
    public MediaDto rename(@PathVariable UUID id, @RequestBody String newName) {
        Media updated = mediaService.rename(id, newName);
        return toDto(updated);
    }

    @PatchMapping("/{id}/status")
    public MediaDto setStatus(
            @PathVariable UUID id,
            @RequestParam MediaStatus status,
            @RequestParam(required = false) String completedAt
    ) {
        java.time.LocalDate completed = (completedAt == null || completedAt.isBlank())
                ? null : java.time.LocalDate.parse(completedAt);
        Media updated = mediaService.setStatus(id, status, completed);
        return toDto(updated);
    }

    @PatchMapping("/{id}/dates")
    public MediaDto setDates(
            @PathVariable UUID id,
            @RequestParam(required = false) String startedAt,
            @RequestParam(required = false) String completedAt
    ) {
        java.time.LocalDate started = (startedAt == null || startedAt.isBlank())
                ? null : java.time.LocalDate.parse(startedAt);
        java.time.LocalDate completed = (completedAt == null || completedAt.isBlank())
                ? null : java.time.LocalDate.parse(completedAt);
        Media updated = mediaService.setDates(id, started, completed);
        return toDto(updated);
    }

    @PatchMapping("/{id}/image-url")
    public MediaDto setImageUrl(@PathVariable UUID id, @RequestBody(required = false) String imageUrl) {
        Media updated = mediaService.setImageUrl(id, imageUrl);
        return toDto(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        mediaService.delete(id);
    }

    private MediaDto toDto(Media m) {
        MediaDto dto = new MediaDto();
        dto.setId(m.getId());
        dto.setName(m.getName());
        dto.setImageUrl(m.getImageUrl());
        dto.setType(m.getType());
        dto.setStatus(m.getStatus());
        dto.setStartedAt(m.getStartedAt());
        dto.setCompletedAt(m.getCompletedAt());
        dto.setCreatedAt(m.getCreatedAt());
        dto.setUpdatedAt(m.getUpdatedAt());

        if (m.getBookDetails() != null) {
            BookDetailsDto bookDto = new BookDetailsDto();
            bookDto.setMediaId(m.getBookDetails().getMediaId());
            bookDto.setAuthor(m.getBookDetails().getAuthor());
            dto.setBookDetails(bookDto);
        }

        if (m.getGameDetails() != null) {
            GameDetailsDto gameDto = new GameDetailsDto();
            gameDto.setMediaId(m.getGameDetails().getMediaId());
            gameDto.setPlatform(m.getGameDetails().getPlatform());
            dto.setGameDetails(gameDto);
        }

        return dto;
    }

}
