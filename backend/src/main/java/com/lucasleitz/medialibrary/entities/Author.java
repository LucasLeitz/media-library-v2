package com.lucasleitz.medialibrary.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "author",
        uniqueConstraints = @UniqueConstraint(name="uq_author_name", columnNames = "name"))
public class Author {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @PrePersist void pre() { if (id == null) id = UUID.randomUUID(); }

    public Author() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

}