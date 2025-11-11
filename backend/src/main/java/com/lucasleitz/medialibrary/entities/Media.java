package com.lucasleitz.medialibrary.entities;

import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }

    @OneToOne(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
    private BookDetails bookDetails;

    @OneToOne(mappedBy = "media", cascade = CascadeType.ALL, orphanRemoval = true)
    private GameDetails gameDetails;

    public Media() {}

    public Media(MediaType type, String name, MediaStatus status) {
        this.type = type;
        this.name = name;
        this.status = status;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public MediaType getType() { return type; }
    public void setType(MediaType type) { this.type = type; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MediaStatus getStatus() { return status; }
    public void setStatus(MediaStatus status) { this.status = status; }

    public LocalDate getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDate startedAt) { this.startedAt = startedAt; }

    public LocalDate getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDate completedAt) { this.completedAt = completedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Media other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Media{id=" + id + ", type=" + type + ", name='" + name + "'}";
    }
}
