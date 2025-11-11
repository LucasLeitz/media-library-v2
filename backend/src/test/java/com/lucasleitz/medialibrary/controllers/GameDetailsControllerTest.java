package com.lucasleitz.medialibrary.controllers;

import com.lucasleitz.medialibrary.entities.GameDetails;
import com.lucasleitz.medialibrary.enums.GamePlatform;
import com.lucasleitz.medialibrary.services.GameDetailsService;
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
@WebMvcTest(controllers = GameDetailsController.class)
@Import(ApiExceptionHandler.class)
class GameDetailsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GameDetailsService gameDetailsService;

    @Test
    void getReturnsDtoWhenFound() throws Exception {
        UUID mediaId = UUID.randomUUID();

        GameDetails gd = new GameDetails();
        gd.setMediaId(mediaId);

        when(gameDetailsService.findByMediaId(mediaId)).thenReturn(Optional.of(gd));

        mvc.perform(get("/gamedetails/{mediaId}", mediaId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.mediaId").value(mediaId.toString()));
    }

    @Test
    void getReturns404WhenNotFound() throws Exception {
        UUID mediaId = UUID.randomUUID();
        when(gameDetailsService.findByMediaId(mediaId)).thenReturn(Optional.empty());

        mvc.perform(get("/gamedetails/{mediaId}", mediaId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    void listByPlatformReturns400WhenPlatformMissing() throws Exception {
        mvc.perform(get("/gamedetails"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.error").value("BAD_REQUEST"));
    }

    @Test
    void createReturns201AndDtoWithPlatform() throws Exception {
        UUID mediaId = UUID.randomUUID();

        GameDetails gd = new GameDetails();
        gd.setMediaId(mediaId);
        gd.setPlatform(GamePlatform.PC);

        when(gameDetailsService.create(mediaId, GamePlatform.PC)).thenReturn(gd);

        mvc.perform(MockMvcRequestBuilders
                        .post("/gamedetails/{mediaId}", mediaId)
                        .queryParam("platform", "PC"))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.mediaId").value(mediaId.toString()))
                .andExpect(jsonPath("$.platform").value("PC"));
    }

    @Test
    void listByPlatformReturnsArrayWhenPlatformProvided() throws Exception {
        var platform = GamePlatform.PC;

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        var g1 = new GameDetails();
        g1.setMediaId(id1);
        g1.setPlatform(platform);

        var g2 = new GameDetails();
        g2.setMediaId(id2);
        g2.setPlatform(platform);

        when(gameDetailsService.listByPlatform(platform)).thenReturn(java.util.List.of(g1, g2));

        mvc.perform(get("/gamedetails").queryParam("platform", "PC"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].mediaId").value(id1.toString()))
                .andExpect(jsonPath("$[0].platform").value("PC"))
                .andExpect(jsonPath("$[1].mediaId").value(id2.toString()))
                .andExpect(jsonPath("$[1].platform").value("PC"));
    }

    @Test
    void setPlatform_updatesAndReturnsDto() throws Exception {
        UUID mediaId = UUID.randomUUID();

        var updated = new GameDetails();
        updated.setMediaId(mediaId);
        updated.setPlatform(GamePlatform.GAMEBOY_ADVANCE);

        when(gameDetailsService.setPlatform(mediaId, GamePlatform.GAMEBOY_ADVANCE))
                .thenReturn(updated);

        mvc.perform(MockMvcRequestBuilders
                        .patch("/gamedetails/{mediaId}/platform", mediaId)
                        .queryParam("platform", "GAMEBOY_ADVANCE"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.mediaId").value(mediaId.toString()))
                .andExpect(jsonPath("$.platform").value("GAMEBOY_ADVANCE"));
    }
}
