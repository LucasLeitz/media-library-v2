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
@Table(name = "book_details")
public class BookDetails {

    @Id
    @Column(name = "media_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID mediaId; // shared PK = FK to media.id

    @OneToOne
    @MapsId
    @JoinColumn(name = "media_id")
    private Media media;

    // cached string like "Stephen King; Peter Straub"
    @Column(name = "author_display", length = 512)
    private String authorDisplay;
}