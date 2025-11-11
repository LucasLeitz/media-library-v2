package com.lucasleitz.medialibrary.services.impl;

import com.lucasleitz.medialibrary.entities.GameDetails;
import com.lucasleitz.medialibrary.enums.GamePlatform;
import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.MediaType;
import com.lucasleitz.medialibrary.repositories.GameDetailsRepository;
import com.lucasleitz.medialibrary.repositories.MediaRepository;
import com.lucasleitz.medialibrary.services.GameDetailsService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class GameDetailsServiceImpl implements GameDetailsService {

    private final GameDetailsRepository gameDetailsRepository;
    private final MediaRepository mediaRepository;

    public GameDetailsServiceImpl(GameDetailsRepository gameDetailsRepository,
                                  MediaRepository mediaRepository) {
        this.gameDetailsRepository = gameDetailsRepository;
        this.mediaRepository = mediaRepository;
    }

    @Override
    public GameDetails create(UUID mediaId, GamePlatform platform) {
        Media media = mediaRepository.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("Media not found: " + mediaId));

        if (media.getType() != MediaType.GAME) {
            throw new IllegalStateException("Media " + mediaId + " is not of type GAME");
        }

        if (gameDetailsRepository.existsById(mediaId)) {
            throw new IllegalStateException("GameDetails already exists for media " + mediaId);
        }

        GameDetails gd = new GameDetails();
        gd.setMedia(media);
        gd.setPlatform(platform);
        return gameDetailsRepository.save(gd);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GameDetails> findByMediaId(UUID mediaId) {
        return gameDetailsRepository.findById(mediaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameDetails> listByPlatform(GamePlatform platform) {
        return gameDetailsRepository.findByPlatform(platform);
    }

    @Override
    public GameDetails setPlatform(UUID mediaId, GamePlatform newPlatform) {
        GameDetails gd = gameDetailsRepository.findById(mediaId)
                .orElseThrow(() -> new EntityNotFoundException("GameDetails not found for media: " + mediaId));
        gd.setPlatform(newPlatform);
        return gameDetailsRepository.save(gd);
    }

}
