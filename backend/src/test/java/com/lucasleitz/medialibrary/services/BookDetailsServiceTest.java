package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.BookDetails;
import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;
import com.lucasleitz.medialibrary.repositories.BookDetailsRepository;
import com.lucasleitz.medialibrary.support.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class BookDetailsServiceTest extends IntegrationTestBase {

    @Autowired
    private EntityManager em;

    @Autowired
    private BookDetailsService bookDetailsService;

    @Autowired
    private BookDetailsRepository bookDetailsRepository;

    @Autowired
    private MediaService mediaService;

    private BookDetails reload(UUID mediaId) {
        em.flush();
        em.clear();
        return bookDetailsRepository.findById(mediaId).orElseThrow();
    }

    private Media createBookMedia(String name) {
        Media m = mediaService.create(
                MediaType.BOOK,
                null,
                name,
                MediaStatus.BACKLOG,
                null,
                null
        );
        em.flush();
        return m;
    }

    @Test
    void createPersistsBookDetailsForBookMedia() {
        Media media = createBookMedia("Dracula");
        String author = "Bram Stoker";

        BookDetails created = bookDetailsService.create(media.getId(), author);

        assertNotNull(created.getMedia());
        assertEquals(media.getId(), created.getMedia().getId());
        assertEquals(author, created.getAuthor());

        BookDetails reloaded = reload(media.getId());
        assertEquals(media.getId(), reloaded.getMedia().getId());
        assertEquals(author, reloaded.getAuthor());
    }

    @Test
    void createAllowsNullAuthor() {
        Media media = createBookMedia("Unnamed Author Book");

        BookDetails created = bookDetailsService.create(media.getId(), null);

        assertNotNull(created.getMedia());
        assertEquals(media.getId(), created.getMedia().getId());
        assertNull(created.getAuthor());

        BookDetails reloaded = reload(media.getId());
        assertEquals(media.getId(), reloaded.getMedia().getId());
        assertNull(reloaded.getAuthor());
    }

    @Test
    void createThrowsWhenMediaNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> bookDetailsService.create( UUID.randomUUID(), "Some Author"));
    }

    @Test
    void createThrowsWhenMediaIsNotBook() {
        Media nonBook = mediaService.create(
                MediaType.GAME,
                null,
                "Hades",
                MediaStatus.BACKLOG,
                null,
                null
        );

        assertThrows(IllegalStateException.class,
                () -> bookDetailsService.create(nonBook.getId(), "Some Author"));
    }

    @Test
    void createThrowsWhenBookDetailsAlreadyExists() {
        Media media = createBookMedia("Dune");
        String author = "Frank Herbert";

        bookDetailsService.create(media.getId(), author);

        assertThrows(IllegalStateException.class,
                () -> bookDetailsService.create(media.getId(), author));
    }

    @Test
    void findByMediaIdReturnsPresentWhenExists() {
        Media media = createBookMedia("Frankenstein");
        String author = "Mary Shelley";

        BookDetails created = bookDetailsService.create(media.getId(), author);

        var found = bookDetailsService.findByMediaId(media.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getMedia().getId(), found.get().getMedia().getId());
        assertEquals(author, found.get().getAuthor());
    }

    @Test
    void findByMediaIdReturnsEmptyWhenMissing() {
        var result = bookDetailsService.findByMediaId(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    void setAuthorUpdatesAuthorAndPersists() {
        Media media = createBookMedia("The Hobbit");
        bookDetailsService.create(media.getId(), "Initial Author");

        BookDetails updated = bookDetailsService.setAuthor(media.getId(), "J.R.R. Tolkien");
        assertEquals("J.R.R. Tolkien", updated.getAuthor());

        BookDetails reloaded = reload(media.getId());
        assertEquals("J.R.R. Tolkien", reloaded.getAuthor());
    }

    @Test
    void setAuthorAllowsNullToClearAuthor() {
        Media media = createBookMedia("Mystery Author");
        bookDetailsService.create(media.getId(), "Someone");

        BookDetails updated = bookDetailsService.setAuthor(media.getId(), null);
        assertNull(updated.getAuthor());

        BookDetails reloaded = reload(media.getId());
        assertNull(reloaded.getAuthor());
    }

    @Test
    void setAuthorThrowsWhenBookDetailsNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> bookDetailsService.setAuthor(UUID.randomUUID(), "Nobody"));
    }
}
