package com.lucasleitz.medialibrary.entities;

import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

    @Test
    void constructorWithArgsSetsBasicFields() {
        Media m = new Media(MediaType.BOOK, "Dune", MediaStatus.BACKLOG);

        assertEquals(MediaType.BOOK, m.getType());
        assertEquals("Dune", m.getName());
        assertEquals(MediaStatus.BACKLOG, m.getStatus());

        // other fields should be unset / null initially
        assertNull(m.getId());
        assertNull(m.getImageUrl());
        assertNull(m.getStartedAt());
        assertNull(m.getCompletedAt());
        assertNull(m.getCreatedAt());
        assertNull(m.getUpdatedAt());
    }

    @Test
    void gettersAndSettersWorkForAllFields() {
        UUID id = UUID.randomUUID();
        MediaType type = MediaType.GAME;
        String imageUrl = "https://example.com/image.png";
        String name = "Hades";
        MediaStatus status = MediaStatus.COMPLETED;
        LocalDate started = LocalDate.of(2024, 1, 10);
        LocalDate completed = LocalDate.of(2024, 2, 10);
        Instant created = Instant.parse("2024-01-01T00:00:00Z");
        Instant updated = Instant.parse("2024-01-02T00:00:00Z");

        Media m = new Media();
        m.setId(id);
        m.setType(type);
        m.setImageUrl(imageUrl);
        m.setName(name);
        m.setStatus(status);
        m.setStartedAt(started);
        m.setCompletedAt(completed);
        m.setCreatedAt(created);
        m.setUpdatedAt(updated);

        assertEquals(id, m.getId());
        assertEquals(type, m.getType());
        assertEquals(imageUrl, m.getImageUrl());
        assertEquals(name, m.getName());
        assertEquals(status, m.getStatus());
        assertEquals(started, m.getStartedAt());
        assertEquals(completed, m.getCompletedAt());
        assertEquals(created, m.getCreatedAt());
        assertEquals(updated, m.getUpdatedAt());
    }

    // -------- equals / hashCode / toString ----------

    @Test
    void equalsIsReflexive() {
        Media m = new Media();
        assertEquals(m, m); // this == o branch
    }

    @Test
    void equalsReturnsFalseForNullAndDifferentType() {
        Media m = new Media();
        assertNotEquals(null, m);
        assertNotEquals(m, "not a media");
    }

    @Test
    void equalsAndHashCodeUseId() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Media m1 = new Media();
        Media m2 = new Media();
        Media m3 = new Media();

        m1.setId(id1);
        m2.setId(id1); // same id as m1
        m3.setId(id2); // different id

        assertEquals(m1, m2);
        assertEquals(m1.hashCode(), m2.hashCode());

        assertNotEquals(m1, m3);
        assertNotEquals(m1.hashCode(), m3.hashCode());
    }

    @Test
    void hashCodeWorksWhenIdIsNull() {
        Media m = new Media();
        // just ensure it doesn't throw
        int hash = m.hashCode();
        // call twice to hit any branch caching / null handling
        int hash2 = m.hashCode();
        assertEquals(hash, hash2);
    }

    @Test
    void toStringReturnsNonEmptyString() {
        Media m = new Media(MediaType.MOVIE, "Inception", MediaStatus.BACKLOG);
        String s = m.toString();
        assertNotNull(s);
        assertFalse(s.isBlank());
        // optional: sanity check presence of name/type
        assertTrue(s.contains("Inception"));
    }

    // -------- lifecycle callbacks: onCreate / onUpdate ----------

    @Test
    void onCreateSetsTimestampsWhenNull() {
        Media m = new Media();

        assertNull(m.getCreatedAt());
        assertNull(m.getUpdatedAt());

        m.onCreate();

        assertNotNull(m.getCreatedAt());
        assertNotNull(m.getUpdatedAt());
    }

    @Test
    void onCreateCanBeCalledWhenTimestampsAlreadySet() {
        Media m = new Media();
        Instant created = Instant.parse("2024-03-01T00:00:00Z");
        Instant updated = Instant.parse("2024-03-02T00:00:00Z");

        m.setCreatedAt(created);
        m.setUpdatedAt(updated);

        // We don't assert specific behavior, just that it doesn’t blow up.
        m.onCreate();
    }

    @Test
    void onUpdateUpdatesTimestamp() {
        Media m = new Media();
        assertNull(m.getUpdatedAt());

        m.onUpdate();
        assertNotNull(m.getUpdatedAt());

        Instant first = m.getUpdatedAt();
        m.onUpdate();
        Instant second = m.getUpdatedAt();

        // It's okay if they're equal or later; main thing is the call works.
        assertNotNull(second);
    }
}

