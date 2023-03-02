package ru.practicum.shareit.requestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.controller.BookingController.HEADER;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {
    @InjectMocks
    RequestController requestController;
    @Mock
    RequestService requestService;
    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;
    private ItemDto itemDto;
    private RequestDto requestDto;
    private final Long userId = 1L;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(false)
                .build();
        requestDto = RequestDto.builder()
                .id(1L)
                .description("itemRequest description")
                .created(LocalDateTime.now().plusDays(1))
                .build();
    }

    @SneakyThrows
    @Test
    void saveRequestTest() {
        when(requestService.saveRequest(anyLong(), any()))
                .thenReturn(requestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemDto))
                        .header(HEADER, userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void findAllByIdTest() {
        when(requestService.findAllByUserId(userId))
                .thenReturn(Collections.singletonList(requestDto));
        mvc.perform(get("/requests")
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void findAllFromSizeTest() {
        when(requestService.findAllFromSize(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(requestDto));
        mvc.perform(get("/requests/all")
                        .header(HEADER, 1L)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(requestDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void findByIdTest() {
        when(requestService.getRequestById(anyLong(), anyLong()))
                .thenReturn((requestDto));
        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(HEADER, 1L)
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(requestDto.getDescription())));
    }
}