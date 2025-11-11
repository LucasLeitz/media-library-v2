package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.Author;

import java.util.*;

public interface AuthorService {
    Author create(String name);
    Author getOrCreate(String name);

    Optional<Author> findById(UUID id);
    Optional<Author> findByName(String name);
    List<Author> listAll();

    List<Author> search(String namePart);

    Author rename(UUID id, String newName);
    void delete(UUID id);
}
