package com.lucasleitz.medialibrary.repositories;

import com.lucasleitz.medialibrary.entities.GameDetails;
import com.lucasleitz.medialibrary.enums.GamePlatform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GameDetailsRepository extends JpaRepository<GameDetails, UUID> {
    List<GameDetails> findByPlatform(GamePlatform platform);
}