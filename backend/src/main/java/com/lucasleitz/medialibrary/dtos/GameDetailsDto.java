package com.lucasleitz.medialibrary.dtos;

import com.lucasleitz.medialibrary.enums.GamePlatform;
import lombok.Data;
import java.util.UUID;

@Data
public class GameDetailsDto {
    private UUID mediaId;
    private GamePlatform platform;
}
