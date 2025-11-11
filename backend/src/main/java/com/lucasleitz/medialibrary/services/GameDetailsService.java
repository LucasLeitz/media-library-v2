package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.GameDetails;
import com.lucasleitz.medialibrary.enums.GamePlatform;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameDetailsService {
    GameDetails create(UUID mediaId, GamePlatform platform);
    Optional<GameDetails> findByMediaId(UUID mediaId);
    List<GameDetails> listByPlatform(GamePlatform platform);
    GameDetails setPlatform(UUID mediaId, GamePlatform newPlatform);
}
