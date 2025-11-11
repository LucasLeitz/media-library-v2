package com.lucasleitz.medialibrary.dtos;

import lombok.Data;
import java.util.UUID;

@Data
public class BookDetailsDto {
    private UUID mediaId;
    private String authorDisplay;
}
