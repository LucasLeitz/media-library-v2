package com.lucasleitz.medialibrary.dtos;

import java.util.UUID;

public class BookDetailsDto {
    private UUID mediaId;
    private String author;

    public UUID getMediaId() {
        return mediaId;
    }

    public void setMediaId(UUID mediaId) {
        this.mediaId = mediaId;
    }

    public String getAuthor(){
        return author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

}
