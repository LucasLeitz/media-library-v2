package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;
import com.lucasleitz.medialibrary.repositories.MediaRepository;
import com.lucasleitz.medialibrary.support.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
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

    private Media reload(UUID id) {
        em.flush();
        em.clear();
        return mediaRepository.findById(id).orElseThrow();
    }

    private Media persistMedia(MediaType type, String name, MediaStatus status, LocalDate started, LocalDate completed) {
        Media m = mediaService.create(type, null, name, status, started, completed);
        mediaRepository.saveAndFlush(m);
        em.clear();
        return m;
    }

    // -------- Create Tests -------------
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
    void createBacklogClearsCompletedAtAndStartedAt() {
        var m = persistMedia(MediaType.BOOK, "Reading Now", MediaStatus.BACKLOG, LocalDate.now(), LocalDate.now());
        assertEquals(MediaStatus.BACKLOG, m.getStatus());
        assertNull(m.getStartedAt());
        assertNull(m.getCompletedAt());
    }

    @Test
    void createInProgressSetsStartedNowWhenNullAndClearsCompleted() {
        var today = LocalDate.now();
        var m = persistMedia(MediaType.BOOK, "Reading Now", MediaStatus.IN_PROGRESS, null, today);
        assertEquals(MediaStatus.IN_PROGRESS, m.getStatus());
        assertEquals(today, m.getStartedAt());
        assertNull(m.getCompletedAt());
    }

    @Test
    void createCompletedRequiresCompletedAt() {
        assertThrows(IllegalArgumentException.class, () ->
                persistMedia(MediaType.GAME, "Finished Game", MediaStatus.COMPLETED, null, null)
        );
    }

    @Test
    void createCompletedWithCompletedAtSetsDates() {
        var m = persistMedia(MediaType.BOOK, "Finished Book", MediaStatus.COMPLETED,
                LocalDate.parse("2024-01-10"), LocalDate.parse("2024-02-10"));
        assertEquals(MediaStatus.COMPLETED, m.getStatus());
        assertEquals(LocalDate.parse("2024-01-10"), m.getStartedAt());
        assertEquals(LocalDate.parse("2024-02-10"), m.getCompletedAt());
    }

    @Test
    void createCompletedWithCompletedBeforeStartedThrows() {
        var started = LocalDate.parse("2024-03-10");
        var completed = LocalDate.parse("2024-03-05"); // before start

        assertThrows(IllegalArgumentException.class, () ->
                mediaService.create(
                        MediaType.BOOK, null, "Bad Order",
                        MediaStatus.COMPLETED, started, completed
                )
        );
    }

    @Test
    void equalDatesAreAllowedForCompleted() {
        var day = LocalDate.parse("2024-06-15");
        Media m = mediaService.create(
                MediaType.BOOK, null, "One Day Read",
                MediaStatus.COMPLETED, day, day
        );

        assertEquals(day, m.getStartedAt());
        assertEquals(day, m.getCompletedAt());
    }

    //----------Set Status Tests------------
    @Test
    void setStatusCompletedRequiresCompletedAt() {
        LocalDate start = LocalDate.of(2024, 3, 1);

        var m = persistMedia(MediaType.BOOK, "Book", MediaStatus.IN_PROGRESS, start, null);
        assertThrows(IllegalArgumentException.class, () ->
                mediaService.setStatus(m.getId(), MediaStatus.COMPLETED, null)
        );
    }

    @Test
    void setStatusToInProgressSetsStartedNowIfMissingAndClearsCompleted() {
        var today = LocalDate.now();
        var updated = mediaService.setStatus(savedId, MediaStatus.IN_PROGRESS, today);

        assertEquals(MediaStatus.IN_PROGRESS, updated.getStatus());
        assertEquals(today, updated.getStartedAt());
        assertNull(updated.getCompletedAt());
    }

    @Test
    void setStatusToInProgressFromCompletedKeepsStartedNowAndClearsCompleted() {
        var startedAt = LocalDate.parse("2024-01-10");
        var m = persistMedia(MediaType.BOOK, "Finished Book",
                MediaStatus.COMPLETED, startedAt,  LocalDate.parse("2024-02-10"));
        var updated = mediaService.setStatus(m.getId(), MediaStatus.IN_PROGRESS, LocalDate.now());

        assertEquals(MediaStatus.IN_PROGRESS, updated.getStatus());
        assertEquals(startedAt, updated.getStartedAt());
        assertNull(updated.getCompletedAt());
    }

    @Test
    void setStatusToBacklogClearsDates() {
        var m = persistMedia(MediaType.BOOK, "Finished Book",
                MediaStatus.COMPLETED, LocalDate.parse("2024-01-10"),  LocalDate.parse("2024-02-10"));

        mediaService.setStatus(m.getId(), MediaStatus.BACKLOG, null);
        Media updated = reload(m.getId());

        assertEquals(MediaStatus.BACKLOG, updated.getStatus());
        assertNull(updated.getStartedAt());
        assertNull(updated.getCompletedAt());
    }

    @Test
    void setStatusToCompletedSetsCompletedAtAndKeepsStartedAt() {
        LocalDate start = LocalDate.of(2024, 3, 1);
        LocalDate done = LocalDate.of(2024, 3, 20);

        var m = persistMedia(MediaType.BOOK, "Book", MediaStatus.IN_PROGRESS, start, null);
        mediaService.setStatus(m.getId(), MediaStatus.COMPLETED, done);
        Media updated = reload(m.getId());

        assertEquals(MediaStatus.COMPLETED, updated.getStatus());
        assertEquals(updated.getStartedAt(), start);
        assertEquals(updated.getCompletedAt(), done);
    }

    @Test
    void setStatusToCompletedWithCompletedBeforeExistingStartedThrows() {
        var started = LocalDate.parse("2024-04-01");
        Media m = mediaService.create(
                MediaType.GAME, null, "Progressing",
                MediaStatus.IN_PROGRESS, started, null
        );

        var badCompleted = LocalDate.parse("2024-03-31");

        assertThrows(IllegalArgumentException.class, () ->
                mediaService.setStatus(m.getId(), MediaStatus.COMPLETED, badCompleted)
        );
    }

    //---------------Set Dates Tests--------------------
    @Test
    void setDatesBacklogAlwaysClearsDates() {
        Media base = persistMedia(MediaType.BOOK, "Book", MediaStatus.BACKLOG,
                LocalDate.of(2020,1,1), LocalDate.of(2020,2,2));

        mediaService.setDates(base.getId(), LocalDate.of(2024,1,1), LocalDate.of(2024,1,2));
        Media updated = reload(base.getId());

        assertEquals(MediaStatus.BACKLOG, updated.getStatus());
        assertNull(updated.getStartedAt());
        assertNull(updated.getCompletedAt());
    }

    @Test
    void setDatesInProgressSetsProvidedStartAndClearsCompleted() {
        Media base = persistMedia(MediaType.BOOK, "Frankenstein", MediaStatus.IN_PROGRESS,
                null, LocalDate.of(2024,4,10));

        LocalDate newStart = LocalDate.of(2024, 4, 1);
        mediaService.setDates(base.getId(), newStart, null);
        Media updated = reload(base.getId());

        assertEquals(MediaStatus.IN_PROGRESS, updated.getStatus());
        assertEquals(newStart, updated.getStartedAt());
        assertNull(updated.getCompletedAt(), "setDates should clear completedAt in IN_PROGRESS");
    }

    @Test
    void setDatesInProgressNoStartProvidesStart() {
        Media base = persistMedia(MediaType.BOOK, "Dracula", MediaStatus.IN_PROGRESS,
                null, null);

        mediaService.setDates(base.getId(), null, null);
        Media updated = reload(base.getId());

        assertEquals(MediaStatus.IN_PROGRESS, updated.getStatus());
        assertEquals(updated.getStartedAt(), LocalDate.now());
        assertNull(updated.getCompletedAt());
    }

    @Test
    void setDatesCompletedUpdatesProvidedDates() {
        Media base = persistMedia(MediaType.BOOK, "The Witcher 3", MediaStatus.COMPLETED, null, LocalDate.of(2024,6,30));

        LocalDate start = LocalDate.of(2024, 6, 1);
        LocalDate done  = LocalDate.of(2024, 7, 1);
        mediaService.setDates(base.getId(), start, done);
        Media updated = reload(base.getId());

        assertEquals(MediaStatus.COMPLETED, updated.getStatus());
        assertEquals(start, updated.getStartedAt());
        assertEquals(done, updated.getCompletedAt());
    }

    @Test
    void setDatesOnCompletedWithCompletedBeforeStartedThrows() {
        var started = LocalDate.parse("2024-05-01");
        var completed = LocalDate.parse("2024-05-10");
        Media m = mediaService.create(
                MediaType.MOVIE, null, "Good Order",
                MediaStatus.COMPLETED, started, completed
        );

        var newStarted = LocalDate.parse("2024-05-05");
        var newCompleted = LocalDate.parse("2024-05-04");

        assertThrows(IllegalArgumentException.class, () ->
                mediaService.setDates(m.getId(), newStarted, newCompleted)
        );
    }

    // ---------- not found guardrail tests ---------
    @Test
    void setStatusThrowsWhenMediaNotFound() {
        UUID missing = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class,
                () -> mediaService.setStatus(missing, MediaStatus.BACKLOG, null));
    }

    @Test
    void setDatesThrowsWhenMediaNotFound() {
        UUID missing = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class,
                () -> mediaService.setDates(missing, null, null));
    }

    @Test
    void renameThrowsWhenIdNotFound() {
        UUID missing = UUID.randomUUID();
        assertThrows(EntityNotFoundException.class,
                () -> mediaService.rename(missing, "Doesn't Matter"));
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
    void searchByNameReturnsCaseInsensitiveMatches() {
        var m1 = persistMedia(MediaType.BOOK,"Dune", MediaStatus.BACKLOG, null, null);
        var m2 = persistMedia(MediaType.BOOK,"Dune Messiah", MediaStatus.BACKLOG, null, null);
        persistMedia(MediaType.BOOK,"Foundation", MediaStatus.BACKLOG, null, null);

        List<Media> results = mediaService.searchByName("dune");
        var ids = results.stream().map(Media::getId).toList();

        assertEquals(2, results.size());
        assertTrue(ids.contains(m1.getId()));
        assertTrue(ids.contains(m2.getId()));
    }

    @Test
    void listByTypeReturnsOnlyThatType() {
        var g = persistMedia(MediaType.GAME,"Hades", MediaStatus.BACKLOG, null, null);
        persistMedia(MediaType.BOOK,"Dune", MediaStatus.BACKLOG, null, null);

        var games = mediaService.listByType(MediaType.GAME);
        assertEquals(1, games.size());
        assertEquals(g.getId(), games.getFirst().getId());
        assertEquals(MediaType.GAME, games.getFirst().getType());
    }

    @Test
    void listByStatusReturnsOnlyThatStatus() {
        var done = persistMedia(MediaType.GAME,"Hades", MediaStatus.COMPLETED, LocalDate.now(), LocalDate.now());
        persistMedia(MediaType.GAME,"Slay the Spire", MediaStatus.BACKLOG, null, null);

        var completed = mediaService.listByStatus(MediaStatus.COMPLETED);
        assertEquals(1, completed.size());
        assertEquals(done.getId(), completed.getFirst().getId());
        assertEquals(MediaStatus.COMPLETED, completed.getFirst().getStatus());
    }

    @Test
    void deleteRemovesEntity() {
        assertTrue(mediaService.findById(savedId).isPresent());

        mediaService.delete(savedId);

        assertTrue(mediaService.findById(savedId).isEmpty());
    }

}
