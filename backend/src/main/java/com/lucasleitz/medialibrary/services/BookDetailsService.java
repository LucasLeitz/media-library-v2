package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.BookDetails;

import java.util.Optional;
import java.util.UUID;

public interface BookDetailsService {

    BookDetails create(UUID mediaId, String author);

    Optional<BookDetails> findByMediaId(UUID mediaId);

    BookDetails setAuthor(UUID mediaId, String author);

}
