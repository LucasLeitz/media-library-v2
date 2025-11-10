package com.lucasleitz.medialibrary.repositories;

import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.entities.MediaStatus;
import com.lucasleitz.medialibrary.entities.MediaType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MediaRepository extends JpaRepository<Media, UUID> {
    List<Media> findByType(MediaType type);
    List<Media> findByStatus(MediaStatus status);
    List<Media> findByTypeAndStatus(MediaType type, MediaStatus status);
    List<Media> findByNameContainingIgnoreCase(String name);
    List<Media> findByStartedAtBetween(LocalDate start, LocalDate end);
}