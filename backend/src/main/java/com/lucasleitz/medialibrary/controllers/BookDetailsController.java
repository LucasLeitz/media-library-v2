package com.lucasleitz.medialibrary.controllers;

import com.lucasleitz.medialibrary.dtos.BookDetailsDto;
import com.lucasleitz.medialibrary.entities.BookDetails;
import com.lucasleitz.medialibrary.services.BookDetailsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bookdetails")
public class BookDetailsController {

    private final BookDetailsService bookDetailsService;

    public BookDetailsController(BookDetailsService bookDetailsService) {
        this.bookDetailsService = bookDetailsService;
    }

    @PostMapping("/{mediaId}")
    public ResponseEntity<BookDetailsDto> create(
            @PathVariable UUID mediaId,
            @RequestParam(required = false) String author // optional on create
    ) {
        BookDetails bd = bookDetailsService.create(mediaId, author);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(bd));
    }

    @GetMapping("/{mediaId}")
    public BookDetailsDto get(@PathVariable UUID mediaId) {
        BookDetails bd = bookDetailsService.findByMediaId(mediaId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("BookDetails not found for media " + mediaId));
        return toDto(bd);
    }

    @PatchMapping("/{mediaId}/author")
    public BookDetailsDto setAuthor(@PathVariable UUID mediaId, @RequestBody String author) {
        BookDetails bd = bookDetailsService.setAuthor(mediaId, author);
        return toDto(bd);
    }

    private BookDetailsDto toDto(BookDetails bd) {
        BookDetailsDto dto = new BookDetailsDto();
        dto.setMediaId(bd.getMediaId());
        dto.setAuthor(bd.getAuthor());
        return dto;
    }
}
