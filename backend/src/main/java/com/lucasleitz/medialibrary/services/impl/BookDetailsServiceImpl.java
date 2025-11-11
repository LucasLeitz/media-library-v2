package com.lucasleitz.medialibrary.services.impl;

import com.lucasleitz.medialibrary.entities.BookDetails;
import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.entities.Author;
import com.lucasleitz.medialibrary.repositories.BookDetailsRepository;
import com.lucasleitz.medialibrary.repositories.MediaRepository;
import com.lucasleitz.medialibrary.services.BookDetailsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
@Transactional
public class BookDetailsServiceImpl implements BookDetailsService {

    private final BookDetailsRepository bookDetailsRepository;
    private final MediaRepository mediaRepository;

    public BookDetailsServiceImpl(BookDetailsRepository bookDetailsRepository,
                                  MediaRepository mediaRepository) {
        this.bookDetailsRepository = bookDetailsRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public BookDetails ensureExists(UUID mediaId) {
        return bookDetailsRepository.findById(mediaId)
                .orElseGet(() -> {
                    Media media = mediaRepository.findById(mediaId)
                            .orElseThrow(() -> new EntityNotFoundException("Media not found: " + mediaId));
                    BookDetails bd = new BookDetails();
                    bd.setMedia(media);
                    return bookDetailsRepository.save(bd);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDetails> findByMediaId(UUID mediaId) {
        return bookDetailsRepository.findById(mediaId);
    }

    @Override
    public BookDetails setAuthorDisplay(UUID mediaId, String authorDisplay) {
        BookDetails bd = ensureExists(mediaId);
        bd.setAuthorDisplay(authorDisplay);
        return bookDetailsRepository.save(bd);
    }

    @Override
    public BookDetails refreshAuthorDisplayFromAuthors(UUID mediaId) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Media not found: " + mediaId));

        var authors = media.getAuthors() != null ? media.getAuthors() : Collections.<Author>emptySet();

        String display = authors.stream()
                .map(Author::getName)
                .filter(n -> n != null && !n.isBlank())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining("; "));

        BookDetails bd = ensureExists(mediaId);
        bd.setAuthorDisplay(display.isBlank() ? null : display);
        return bookDetailsRepository.save(bd);
    }

    @Override
    public void delete(UUID mediaId) {
        if (!bookDetailsRepository.existsById(mediaId)) {
            throw new EntityNotFoundException("BookDetails not found for media: " + mediaId);
        }
        bookDetailsRepository.deleteById(mediaId);
    }
}
