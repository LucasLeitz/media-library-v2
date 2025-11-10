package com.lucasleitz.medialibrary.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
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
}