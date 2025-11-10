package com.lucasleitz.medialibrary.entities;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
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
}