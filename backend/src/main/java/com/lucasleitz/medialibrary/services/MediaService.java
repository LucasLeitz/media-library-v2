package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaService {

    Media create(MediaType type, String imageUrl, String name, MediaStatus status,
                 LocalDate startedAt, LocalDate completedAt);

    Optional<Media> findById(UUID id);

    List<Media> searchByName(String namePart);

    List<Media> listByType(MediaType type);

    List<Media> listByStatus(MediaStatus status);

    Media rename(UUID id, String newName);

    Media setStatus(UUID id, MediaStatus status, LocalDate completedAt);

    Media setDates(UUID id, LocalDate startedAt, LocalDate completedAt);

    Media setImageUrl(UUID id, String imageUrl);

    void delete(UUID id);

}