package com.lucasleitz.medialibrary.controllers;

import com.lucasleitz.medialibrary.entities.Media;
import com.lucasleitz.medialibrary.enums.MediaStatus;
import com.lucasleitz.medialibrary.enums.MediaType;
import com.lucasleitz.medialibrary.services.MediaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MediaController.class)
@Import(ApiExceptionHandler.class)
class MediaControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MediaService mediaService;

    @Test
    void getReturnsDtoWhenFound() throws Exception {
        UUID id = UUID.randomUUID();
        Media m = new Media();
        m.setId(id);
        m.setName("Blade Runner");

        when(mediaService.findById(id)).thenReturn(Optional.of(m));

        mvc.perform(get("/media/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Blade Runner"));
    }

    @Test
    void getReturns404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(mediaService.findById(id)).thenReturn(Optional.empty());

        mvc.perform(get("/media/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void createReturns201AndDto() throws Exception {
        UUID id = UUID.randomUUID();

        var media = new Media();
        media.setId(id);
        media.setName("The Witcher 3");

        when(mediaService.create(
                MediaType.GAME,
                "http://image.url",
                "The Witcher 3",
                MediaStatus.COMPLETED,
                null,
                null
        )).thenReturn(media);

        mvc.perform(MockMvcRequestBuilders
                        .post("/media")
                        .contentType("application/json")
                        .content("""
                    {
                      "type": "GAME",
                      "imageUrl": "http://image.url",
                      "name": "The Witcher 3",
                      "status": "COMPLETED"
                    }
                    """)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("The Witcher 3"));
    }

    @Test
    void renameUpdatesNameAndReturnsDto() throws Exception {
        UUID id = UUID.randomUUID();

        var updated = new Media();
        updated.setId(id);
        updated.setName("New Title");

        when(mediaService.rename(id, "New Title")).thenReturn(updated);

        mvc.perform(MockMvcRequestBuilders
                        .patch("/media/{id}/name", id)
                        .contentType("text/plain")   // @RequestBody String newName
                        .content("New Title")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("New Title"));
    }

    @Test
    void deleteRemovesMediaAndReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mvc.perform(MockMvcRequestBuilders.delete("/media/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void setStatusUpdatesStatusAndReturnsDto() throws Exception {
        UUID id = UUID.randomUUID();

        var updated = new Media();
        updated.setId(id);
        updated.setName("Blade Runner");
        updated.setStatus(MediaStatus.COMPLETED);
        updated.setCompletedAt(java.time.LocalDate.parse("2024-01-01"));

        when(mediaService.setStatus(
                id,
                MediaStatus.COMPLETED,
                java.time.LocalDate.parse("2024-01-01")
        )).thenReturn(updated);

        mvc.perform(MockMvcRequestBuilders
                        .patch("/media/{id}/status", id)
                        .queryParam("status", "COMPLETED")
                        .queryParam("completedAt", "2024-01-01"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.completedAt").value("2024-01-01"));
    }

    @Test
    void setDatesUpdatesDatesAndReturnsDto() throws Exception {
        UUID id = UUID.randomUUID();

        var updated = new Media();
        updated.setId(id);
        updated.setName("Dune");
        updated.setStartedAt(java.time.LocalDate.parse("2024-02-01"));
        updated.setCompletedAt(java.time.LocalDate.parse("2024-03-15"));

        when(mediaService.setDates(
                id,
                java.time.LocalDate.parse("2024-02-01"),
                java.time.LocalDate.parse("2024-03-15")
        )).thenReturn(updated);

        mvc.perform(MockMvcRequestBuilders
                        .patch("/media/{id}/dates", id)
                        .queryParam("startedAt", "2024-02-01")
                        .queryParam("completedAt", "2024-03-15")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.startedAt").value("2024-02-01"))
                .andExpect(jsonPath("$.completedAt").value("2024-03-15"));
    }

    @Test
    void listReturnsMatchesWhenQueryProvided() throws Exception {
        var m1 = new Media(); m1.setId(UUID.randomUUID()); m1.setName("Dune");
        var m2 = new Media(); m2.setId(UUID.randomUUID()); m2.setName("Dune Messiah");

        when(mediaService.searchByName("dune")).thenReturn(java.util.List.of(m1, m2));

        mvc.perform(get("/media").queryParam("q", "dune"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(m1.getId().toString()))
                .andExpect(jsonPath("$[0].name").value("Dune"))
                .andExpect(jsonPath("$[1].id").value(m2.getId().toString()))
                .andExpect(jsonPath("$[1].name").value("Dune Messiah"));
    }

    @Test
    void listReturnsByTypeWhenTypeProvidedAndNoQuery() throws Exception {
        var type = com.lucasleitz.medialibrary.enums.MediaType.GAME;

        var g = new Media(); g.setId(UUID.randomUUID()); g.setName("Hades");
        when(mediaService.listByType(type)).thenReturn(java.util.List.of(g));

        mvc.perform(get("/media").queryParam("type", "GAME"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Hades"));
    }

    @Test
    void listReturnsByStatusWhenStatusProvidedAndNoQueryOrType() throws Exception {
        var status = MediaStatus.COMPLETED;

        var a = new Media(); a.setId(UUID.randomUUID()); a.setName("The Road");
        when(mediaService.listByStatus(status)).thenReturn(java.util.List.of(a));

        mvc.perform(get("/media").queryParam("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("The Road"));
    }

    @Test
    void listDefaultsToSearchAllWhenNoFiltersProvided() throws Exception {
        var x = new Media(); x.setId(UUID.randomUUID()); x.setName("Anything");
        when(mediaService.searchByName("")).thenReturn(java.util.List.of(x));

        mvc.perform(get("/media"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Anything"));
    }

    @Test
    void setStatus_handlesBlankCompletedAt() throws Exception {
        UUID id = UUID.randomUUID();

        var updated = new Media();
        updated.setId(id);
        updated.setStatus(MediaStatus.BACKLOG);

        when(mediaService.setStatus(id, MediaStatus.BACKLOG, null)).thenReturn(updated);

        mvc.perform(MockMvcRequestBuilders
                        .patch("/media/{id}/status", id)
                        .queryParam("status", "BACKLOG")
                        .queryParam("completedAt", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BACKLOG"));
    }

    @Test
    void setDatesHandlesMissingDates() throws Exception {
        UUID id = UUID.randomUUID();

        var updated = new Media();
        updated.setId(id);

        when(mediaService.setDates(id, null, null)).thenReturn(updated);

        mvc.perform(MockMvcRequestBuilders.patch("/media/{id}/dates", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()));
    }

}
