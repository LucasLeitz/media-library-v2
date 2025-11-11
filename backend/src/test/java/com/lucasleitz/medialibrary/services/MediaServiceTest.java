package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;
import com.lucasleitz.medialibrary.repositories.MediaRepository;
import com.lucasleitz.medialibrary.support.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class MediaServiceTest extends IntegrationTestBase {

    @Autowired
    private MediaService mediaService;

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private EntityManager em;

    private UUID savedId;

    @BeforeEach
    void seedOne() {
        var m = mediaService.create(
                MediaType.BOOK,
                null,
                "Seed Title",
                MediaStatus.BACKLOG,
                null,
                null
        );
        savedId = m.getId();
    }

    @Test
    void createPersistsAndIsFetchable() {

        assertNotNull(savedId, "ID should be generated");

        em.flush();
        em.clear();

        Media reloaded = mediaService.findById(savedId)
                .orElseThrow(() -> new AssertionError("Media not found after save"));

        assertEquals("Seed Title", reloaded.getName());
        assertEquals(MediaType.BOOK, reloaded.getType());
        assertEquals(MediaStatus.BACKLOG, reloaded.getStatus());
        assertNull(reloaded.getStartedAt());
        assertNull(reloaded.getCompletedAt());
        assertNotNull(reloaded.getCreatedAt());
        assertNotNull(reloaded.getUpdatedAt());
    }

    @Test
    void renameUpdatesNamePersists() {
        var updated = mediaService.rename(savedId, "New Name");
        assertEquals("New Name", updated.getName());

        em.flush();
        em.clear();

        var reloaded = mediaRepository.findById(savedId).orElseThrow();
        assertEquals("New Name", reloaded.getName());
    }

    @Test
    void setStatusInProgressSetsStartedAt() {
        var original = mediaService.findById(savedId).orElseThrow(() -> new AssertionError("Media not found"));
        assertNull(original.getStartedAt());

        var updated = mediaService.setStatus(savedId, MediaStatus.IN_PROGRESS, null);
        assertNotNull(updated.getStartedAt());

    }






}
