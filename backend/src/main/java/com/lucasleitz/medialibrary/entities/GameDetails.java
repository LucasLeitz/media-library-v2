package com.lucasleitz.medialibrary.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "game_details")
public class GameDetails {

    @Id
    @Column(name = "media_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID mediaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", nullable = false)
    private GamePlatform platform;

    @OneToOne
    @MapsId
    @JoinColumn(name = "media_id")
    private Media media;

    public GameDetails() {}

    public UUID getMediaId() { return mediaId; }
    public void setMediaId(UUID mediaId) { this.mediaId = mediaId; }

    public GamePlatform getPlatform() { return platform; }
    public void setPlatform(GamePlatform platform) { this.platform = platform; }

    public Media getMedia() { return media; }
    public void setMedia(Media media) { this.media = media; }
}