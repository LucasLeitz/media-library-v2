package com.lucasleitz.medialibrary.dto;

import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class MediaDto {
    private UUID id;
    private String name;
    private String imageUrl;
    private MediaType type;
    private MediaStatus status;
    private LocalDate startedAt;
    private LocalDate completedAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

}