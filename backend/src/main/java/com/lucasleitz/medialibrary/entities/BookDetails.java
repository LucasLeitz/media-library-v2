package com.lucasleitz.medialibrary.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "book_details")
public class BookDetails {

    @Id
    @Column(name = "media_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID mediaId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "media_id")
    private Media media;

    @Column(name = "author_display", length = 512)
    private String authorDisplay;

    public BookDetails() {}

    public UUID getMediaId() { return mediaId; }
    public void setMediaId(UUID mediaId) { this.mediaId = mediaId; }

    public Media getMedia() { return media; }
    public void setMedia(Media media) { this.media = media; }

    public String getAuthorDisplay() { return authorDisplay; }
    public void setAuthorDisplay(String authorDisplay) { this.authorDisplay = authorDisplay; }

}