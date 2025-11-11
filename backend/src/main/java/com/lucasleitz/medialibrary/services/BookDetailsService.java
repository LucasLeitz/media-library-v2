package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.BookDetails;

import java.util.Optional;
import java.util.UUID;

public interface BookDetailsService {

    BookDetails ensureExists(UUID mediaId);

    Optional<BookDetails> findByMediaId(UUID mediaId);

    BookDetails setAuthorDisplay(UUID mediaId, String authorDisplay);

    BookDetails refreshAuthorDisplayFromAuthors(UUID mediaId);

    void delete(UUID mediaId);
}
