package com.lucasleitz.medialibrary.dtos;

import com.lucasleitz.medialibrary.enums.GamePlatform;
import lombok.Data;
import java.util.UUID;

@Data
public class GameDetailsDto {
    private UUID mediaId;
    private GamePlatform platform;

    public UUID getMediaId() {
        return mediaId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
    }

    public GamePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(GamePlatform platform) {
        this.platform = platform;
    }
}
