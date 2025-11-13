package com.lucasleitz.medialibrary.services;

import com.lucasleitz.medialibrary.entities.GameDetails;
import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.GamePlatform;
import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;
import com.lucasleitz.medialibrary.repositories.GameDetailsRepository;
import com.lucasleitz.medialibrary.support.IntegrationTestBase;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class GameDetailsServiceTest extends IntegrationTestBase {

    @Autowired
    private EntityManager em;

    @Autowired
    GameDetailsService gameDetailsService;

    @Autowired
    GameDetailsRepository gameDetailsRepository;

    @Autowired
    MediaService mediaService;

    private GameDetails reload(UUID mediaId) {
        em.flush();
        em.clear();
        return gameDetailsRepository.findById(mediaId).orElseThrow();
    }

    private Media createGameMedia(String name) {
        Media m = mediaService.create( MediaType.GAME, null, name,
                MediaStatus.BACKLOG, null, null);
        em.flush();
        return m;
    }

    @Test
    void createPersistsGameDetailsForGameMedia() {
        Media media = createGameMedia("Hades");
        GamePlatform platform = GamePlatform.PC;

        GameDetails created = gameDetailsService.create(media.getId(), platform);

        assertNotNull(created.getMedia());
        assertEquals(media.getId(), created.getMedia().getId());
        assertEquals(platform, created.getPlatform());

        GameDetails reloaded = reload(media.getId());
        assertEquals(platform, reloaded.getPlatform());
        assertEquals(media.getId(), reloaded.getMedia().getId());
    }

    @Test
    void createThrowsWhenMediaNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> gameDetailsService.create(UUID.randomUUID(), GamePlatform.PC));
    }

    @Test
    void createThrowsWhenMediaIsNotGame() {
        Media nonGame = mediaService.create( MediaType.BOOK,null,
                "Dracula", MediaStatus.BACKLOG, null, null);

        assertThrows(IllegalStateException.class,
                () -> gameDetailsService.create(nonGame.getId(), GamePlatform.PC));
    }

    @Test
    void createThrowsWhenGameDetailsAlreadyExists() {
        Media media = createGameMedia("Hollow Knight");
        GamePlatform platform = GamePlatform.PC;

        gameDetailsService.create(media.getId(), platform);

        assertThrows(IllegalStateException.class,
                () -> gameDetailsService.create(media.getId(), platform));
    }

    @Test
    void findByMediaIdReturnsPresentWhenExists() {
        Media media = createGameMedia("Celeste");
        GamePlatform platform = GamePlatform.PC;

        GameDetails created = gameDetailsService.create(media.getId(), platform);

        var found = gameDetailsService.findByMediaId(media.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getMedia().getId(), found.get().getMedia().getId());
        assertEquals(platform, found.get().getPlatform());
    }

    @Test
    void findByMediaIdReturnsEmptyWhenMissing() {
        var result = gameDetailsService.findByMediaId(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    void listByPlatformReturnsOnlyMatchingPlatform() {
        GamePlatform platform1 = GamePlatform.PC;
        GamePlatform platform2 = GamePlatform.PLAYSTATION_2;

        Media m1 = createGameMedia("Game One");
        Media m2 = createGameMedia("Game Two");
        gameDetailsService.create(m1.getId(), platform1);
        gameDetailsService.create(m2.getId(), platform1);

        Media m3 = createGameMedia("Game Three");
        gameDetailsService.create(m3.getId(), platform2);

        List<GameDetails> results = gameDetailsService.listByPlatform(platform1);

        assertThat(results).hasSize(2);
        var mediaIds = results.stream()
                .map(gd -> gd.getMedia().getId())
                .toList();

        assertThat(mediaIds).containsExactlyInAnyOrder(m1.getId(), m2.getId());
    }


    @Test
    void setPlatformUpdatesPlatformAndPersists() {
        Media media = createGameMedia("Platform Shifter");
        GamePlatform initial = GamePlatform.PC;
        gameDetailsService.create(media.getId(), initial);

        GamePlatform newPlatform = GamePlatform.PLAYSTATION_3;

        GameDetails updated = gameDetailsService.setPlatform(media.getId(), newPlatform);
        assertEquals(newPlatform, updated.getPlatform());

        GameDetails reloaded = reload(media.getId());
        assertEquals(newPlatform, reloaded.getPlatform());
    }

    @Test
    void setPlatformThrowsWhenGameDetailsNotFound() {
        assertThrows(EntityNotFoundException.class,
                () -> gameDetailsService.setPlatform(UUID.randomUUID(), GamePlatform.PC));
    }

}
