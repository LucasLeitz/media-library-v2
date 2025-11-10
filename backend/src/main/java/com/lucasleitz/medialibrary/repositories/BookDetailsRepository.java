package com.lucasleitz.medialibrary.repositories;

import com.lucasleitz.medialibrary.entities.BookDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookDetailsRepository extends JpaRepository<BookDetails, UUID> { }