package com.lucasleitz.medialibrary.controllers;

import com.lucasleitz.medialibrary.dtos.GameDetailsDto;
import com.lucasleitz.medialibrary.entities.GameDetails;
import com.lucasleitz.medialibrary.enums.GamePlatform;
import com.lucasleitz.medialibrary.services.GameDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/gamedetails")
public class GameDetailsController {

    private final GameDetailsService service;

    public GameDetailsController(GameDetailsService service) {
        this.service = service;
    }

    @PostMapping("/{mediaId}")
    public ResponseEntity<GameDetailsDto> create(
            @PathVariable UUID mediaId,
            @RequestParam(required = false) GamePlatform platform) {
        GameDetails gd = service.create(mediaId, platform);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(gd));
    }

    @GetMapping("/{mediaId}")
    public GameDetailsDto get(@PathVariable UUID mediaId) {
        GameDetails gd = service.findByMediaId(mediaId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("GameDetails not found for media " + mediaId));
        return toDto(gd);
    }

    @GetMapping
    public List<GameDetailsDto> listByPlatform(@RequestParam(required = false) GamePlatform platform) {
        if (platform == null) {
            // if you want a list-all behavior, add repo.findAll() to the service; otherwise throw 400
            throw new IllegalArgumentException("platform is required");
        }
        return service.listByPlatform(platform).stream().map(this::toDto).toList();
    }

    @PatchMapping("/{mediaId}/platform")
    public GameDetailsDto setPlatform(@PathVariable UUID mediaId, @RequestParam GamePlatform platform) {
        GameDetails gd = service.setPlatform(mediaId, platform);
        return toDto(gd);
    }

    private GameDetailsDto toDto(GameDetails gd) {
        GameDetailsDto dto = new GameDetailsDto();
        dto.setMediaId(gd.getMediaId());
        dto.setPlatform(gd.getPlatform());
        return dto;
    }
}
