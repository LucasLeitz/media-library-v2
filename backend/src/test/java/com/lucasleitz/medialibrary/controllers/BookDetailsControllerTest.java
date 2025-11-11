package com.lucasleitz.medialibrary.controllers;

import com.lucasleitz.medialibrary.entities.BookDetails;
import com.lucasleitz.medialibrary.services.BookDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BookDetailsController.class)
@Import(ApiExceptionHandler.class)
class BookDetailsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookDetailsService bookDetailsService;

    @Test
    void getReturnsDtoWhenFound() throws Exception {
        UUID mediaId = UUID.randomUUID();

        BookDetails bd = new BookDetails();
        bd.setMediaId(mediaId);
        bd.setAuthor("Toni Morrison");

        when(bookDetailsService.findByMediaId(mediaId)).thenReturn(Optional.of(bd));

        mvc.perform(get("/bookdetails/{mediaId}", mediaId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.mediaId").value(mediaId.toString()))
                .andExpect(jsonPath("$.author").value("Toni Morrison"));
    }

    @Test
    void getReturns404WhenNotFound() throws Exception {
        UUID mediaId = UUID.randomUUID();
        when(bookDetailsService.findByMediaId(mediaId)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders.get("/bookdetails/{mediaId}", mediaId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturns201AndDto() throws Exception {
        UUID mediaId = UUID.randomUUID();

        BookDetails bd = new BookDetails();
        bd.setMediaId(mediaId);
        bd.setAuthor("Toni Morrison");

        when(bookDetailsService.create(mediaId, "Toni Morrison")).thenReturn(bd);

        mvc.perform(MockMvcRequestBuilders
                        .post("/bookdetails/{mediaId}", mediaId)
                        .queryParam("author", "Toni Morrison"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.mediaId").value(mediaId.toString()))
                .andExpect(jsonPath("$.author").value("Toni Morrison"));
    }

    @Test
    void setAuthorUpdatesAndReturnsDto() throws Exception {
        UUID mediaId = UUID.randomUUID();

        BookDetails bd = new BookDetails();
        bd.setMediaId(mediaId);
        bd.setAuthor("George Orwell");

        when(bookDetailsService.setAuthor(mediaId, "George Orwell")).thenReturn(bd);

        mvc.perform(MockMvcRequestBuilders
                        .patch("/bookdetails/{mediaId}/author", mediaId)
                        .content("George Orwell")
                        .contentType("text/plain")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.mediaId").value(mediaId.toString()))
                .andExpect(jsonPath("$.author").value("George Orwell"));
    }
}