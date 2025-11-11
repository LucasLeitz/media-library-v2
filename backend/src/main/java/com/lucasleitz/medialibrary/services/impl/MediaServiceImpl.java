package com.lucasleitz.medialibrary.services.impl;

import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;
import com.lucasleitz.medialibrary.repositories.MediaRepository;
import com.lucasleitz.medialibrary.services.MediaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
    public Media create(MediaType type, String imageUrl, String name, MediaStatus status,
                        LocalDate startedAt, LocalDate completedAt) {
        Media m = new Media();
        m.setType(type);
        m.setImageUrl(imageUrl);
        m.setName(name);
        m.setStatus(status);

        switch (status) {
            case BACKLOG -> {
                m.setStartedAt(null);
                m.setCompletedAt(null);
            }
            case IN_PROGRESS -> {
                m.setStartedAt(startedAt != null ? startedAt : LocalDate.now());
                m.setCompletedAt(null);
            }
            case COMPLETED -> {
                if (completedAt == null) {
                    throw new IllegalArgumentException("completedAt must be provided when status=COMPLETED");
                }
                m.setCompletedAt(completedAt);
                m.setStartedAt(startedAt);
            }
        }

        return mediaRepository.save(m);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Media> findById(UUID id) {
        return mediaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> searchByName(String namePart) {
        return mediaRepository.findByNameContainingIgnoreCase(namePart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> listByType(MediaType type) {
        return mediaRepository.findByType(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Media> listByStatus(MediaStatus status) {
        return mediaRepository.findByStatus(status);
    }

    @Override
    public Media rename(UUID id, String newName) {
        Media m = mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found: " + id));
        m.setName(newName);
        return mediaRepository.save(m);
    }

    @Override
    public Media setStatus(UUID id, MediaStatus status, LocalDate completedAt) {
        Media m = mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found: " + id));

        m.setStatus(status);

        switch (status) {
            case BACKLOG -> {
                m.setStartedAt(null);
                m.setCompletedAt(null);
            }
            case IN_PROGRESS -> {
                if (m.getStartedAt() == null) {
                    m.setStartedAt(LocalDate.now());
                }
                m.setCompletedAt(null);
            }
            case COMPLETED -> {
                if (completedAt == null) {
                    throw new IllegalArgumentException("completedAt must be provided when status=COMPLETED");
                }
                m.setCompletedAt(completedAt);
            }
        }

        return mediaRepository.save(m);
    }

    @Override
    public Media setDates(UUID id, LocalDate startedAt, LocalDate completedAt) {
        Media m = mediaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Media not found: " + id));

        switch (m.getStatus()) {
            case BACKLOG -> {
                m.setStartedAt(null);
                m.setCompletedAt(null);
            }
            case IN_PROGRESS -> {
                m.setStartedAt(startedAt != null ? startedAt : (m.getStartedAt() != null ? m.getStartedAt() : LocalDate.now()));
                m.setCompletedAt(null);
            }
            case COMPLETED -> {
                if (completedAt == null && m.getCompletedAt() == null) {
                    throw new IllegalArgumentException("completedAt is required when status=COMPLETED");
                }
                if (completedAt != null) m.setCompletedAt(completedAt);
                if (startedAt != null) m.setStartedAt(startedAt);
            }
        }

        return mediaRepository.save(m);
    }

    @Override
    public void delete(UUID id) {
        mediaRepository.deleteById(id);
    }
}