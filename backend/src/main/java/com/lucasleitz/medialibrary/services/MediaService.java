package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.entities.MediaStatus;
import com.lucasleitz.medialibrary.entities.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MediaService {

    Media create(MediaType type, String name, MediaStatus status,
                 LocalDate startedOn, LocalDate completedOn);

    Optional<Media> findById(UUID id);

    List<Media> searchByName(String namePart);

    List<Media> listByType(MediaType type);

    List<Media> listByStatus(MediaStatus status);

    Media rename(UUID id, String newName);

    Media setStatus(UUID id, MediaStatus status, LocalDate completedOn);

    Media setDates(UUID id, LocalDate startedOn, LocalDate completedOn);

    void delete(UUID id);

}