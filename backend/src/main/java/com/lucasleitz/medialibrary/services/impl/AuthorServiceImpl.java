package com.lucasleitz.medialibrary.services.impl;

import com.lucasleitz.medialibrary.entities.Author;
import com.lucasleitz.medialibrary.repositories.AuthorRepository;
import com.lucasleitz.medialibrary.services.AuthorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    private static String norm(String s) {
        if (s == null) throw new IllegalArgumentException("name cannot be null");
        String t = s.trim();
        if (t.isEmpty()) throw new IllegalArgumentException("name cannot be blank");
        return t;
    }

    @Override
    public Author create(String name) {
        String n = norm(name);
        if (authorRepository.existsByNameIgnoreCase(n)) {
            throw new DataIntegrityViolationException("Author already exists: " + n);
        }
        Author a = new Author();
        a.setName(n);
        return authorRepository.save(a);
    }

    @Override
    public Author getOrCreate(String name) {
        String n = norm(name);
        return authorRepository.findByNameIgnoreCase(n)
                .orElseGet(() -> {
                    Author a = new Author();
                    a.setName(n);
                    return authorRepository.save(a);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Author> findById(UUID id) {
        return authorRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Author> findByName(String name) {
        return authorRepository.findByNameIgnoreCase(norm(name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> listAll() {
        return authorRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Author> search(String namePart) {
        String q = norm(namePart).toLowerCase();
        return authorRepository.findAll().stream()
                .filter(a -> a.getName() != null && a.getName().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    @Override
    public Author rename(UUID id, String newName) {
        String n = norm(newName);
        authorRepository.findByNameIgnoreCase(n).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DataIntegrityViolationException("Another author already uses name: " + n);
            }
        });

        Author a = authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author not found: " + id));
        a.setName(n);
        return authorRepository.save(a);
    }

    @Override
    public void delete(UUID id) {
        if (!authorRepository.existsById(id)) {
            throw new EntityNotFoundException("Author not found: " + id);
        }
        authorRepository.deleteById(id);
    }
}
