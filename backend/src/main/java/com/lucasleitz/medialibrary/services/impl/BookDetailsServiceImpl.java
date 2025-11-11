package com.lucasleitz.medialibrary.services.impl;

import com.lucasleitz.medialibrary.entities.BookDetails;
import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.MediaType;
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
    public BookDetails create(UUID mediaId, String author) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Media not found: " + mediaId));
        if (media.getType() != MediaType.BOOK) {
            throw new IllegalStateException("Media " + mediaId + " is not of type BOOK");
        }
        if (bookDetailsRepository.existsById(mediaId)) {
            throw new IllegalStateException("BookDetails already exists for media " + mediaId);
        }

        BookDetails bd = new BookDetails();
        bd.setMedia(media);         // @OneToOne @MapsId – mediaId is PK
        bd.setAuthor(author);       // may be null; you can set later
        return bookDetailsRepository.save(bd);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookDetails> findByMediaId(UUID mediaId) {
        return bookDetailsRepository.findById(mediaId);
    }

    @Override
    public BookDetails setAuthor(UUID mediaId, String author) {
        BookDetails bd = bookDetailsRepository.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("BookDetails not found: " + mediaId));
        bd.setAuthor(author);
        return bookDetailsRepository.save(bd);
    }
}
