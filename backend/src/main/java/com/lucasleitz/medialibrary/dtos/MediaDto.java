package com.lucasleitz.medialibrary.dtos;

import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public class MediaDto {
    private UUID id;
    private String name;
    private String imageUrl;
    private MediaType type;
    private MediaStatus status;
    private LocalDate startedAt;
    private LocalDate completedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public MediaDto() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }

    public MediaStatus getStatus() {
        return status;
    }

    public void setStatus(MediaStatus status) {
        this.status = status;
    }

    public LocalDate getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDate startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDate getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDate completedAt) {
        this.completedAt = completedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}