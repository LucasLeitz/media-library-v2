package com.lucasleitz.medialibrary.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter @Setter
@NoArgsConstructor
@Entity
@Table(name = "media")
public class Media {
    @Id
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    private MediaType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private MediaStatus status;

    @Column(name = "started_at")
    private LocalDate startedAt;

    @Column(name = "completed_at")
    private LocalDate completedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID();
        var now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    @ManyToMany
    @JoinTable(
            name = "book_author",
            joinColumns = @JoinColumn(name = "media_id", referencedColumnName = "id", columnDefinition = "BINARY(16)"),
            inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id", columnDefinition = "BINARY(16)")
    )
    private Set<Author> authors = new HashSet<>();

    @OneToOne(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
    private BookDetails bookDetails;

    @OneToOne(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
    private GameDetails gameDetails;

}