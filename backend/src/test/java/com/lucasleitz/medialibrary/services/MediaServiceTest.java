package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;
import com.lucasleitz.medialibrary.support.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
    private EntityManager em;

    @Test
    void createPersistsAndIsFetchable() {

        Media saved = mediaService.create(
                MediaType.BOOK,
                null,
                "East of Eden",
                MediaStatus.BACKLOG,
                null,
                null
        );

        assertNotNull(saved.getId(), "ID should be generated");

        em.flush();
        em.clear();

        Media reloaded = mediaService.findById(saved.getId())
                .orElseThrow(() -> new AssertionError("Media not found after save"));

        assertEquals("East of Eden", reloaded.getName());
        assertEquals(MediaType.BOOK, reloaded.getType());
        assertEquals(MediaStatus.BACKLOG, reloaded.getStatus());
        assertNull(reloaded.getStartedAt());
        assertNull(reloaded.getCompletedAt());
        assertNotNull(reloaded.getCreatedAt());
        assertNotNull(reloaded.getUpdatedAt());
    }






}
