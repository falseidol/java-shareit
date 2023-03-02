package ru.practicum.shareit.itemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.controller.BookingController.HEADER;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {
    @Mock
    ItemService itemService;
    @InjectMocks
    ItemController itemController;
    private MockMvc mvc;
    private final ObjectMapper mapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private UserDto userDto;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private final long itemId = 1L;
    private final long userId = 1L;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .name("userName")
                .email("name@mail.com")
                .build();
        itemDto = ItemDto.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(false)
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .text("comment text")
                .authorName("antony")
                .build();
    }

    @SneakyThrows
    @Test
    void addItemValidFields() {
        ItemDto itemToSave = ItemDto.builder()
                .name("item name")
                .description("item description")
                .available(false)
                .build();
        when(itemService.addItem(anyLong(), any()))
                .thenReturn(itemDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemToSave))
                        .header(HEADER, itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void addItemNonValidFieldsReturnsBadRequest() {
        ItemDto itemDto1 = ItemDto.builder().build();
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto1))
                        .header(HEADER, itemId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getById_whenInvoked_thenGetItemDtoResponse() {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(itemId)
                .name("item name")
                .description("item description")
                .available(false)
                .owner(UserMapper.mapToNewUser(userDto))
                .build();
        when(itemService.getItemById(userId, itemId))
                .thenReturn(itemDtoResponse);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @SneakyThrows
    @Test
    void updateItemWithNotNullFields() {
        ItemDto itemUpdate = ItemDto.builder()
                .name("item name")
                .description("item description")
                .available(false)
                .build();
        when(itemService.updateItem(anyLong(), any(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/{itemId}", itemId)
                        .header(HEADER, userId)
                        .content(mapper.writeValueAsString(itemUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));
    }

    @SneakyThrows
    @Test
    void findAllWhenEmptyList() {
        when(itemService.findAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(Collections.emptyList());
        mvc.perform(get("/items")
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @SneakyThrows
    @Test
    void searchItem() {
        when(itemService.searchItem(anyInt(), anyInt(), anyString()))
                .thenReturn(Collections.singletonList(itemDto));
        mvc.perform(get("/items/search")
                        .param("text", "ABOBKA")
                        .param("from", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(itemDto.getName())))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @SneakyThrows
    @Test
    void getItemByIdAndUserId() {
        ItemDtoResponse itemDtoResponse = ItemDtoResponse.builder()
                .id(itemId)
                .name("item name")
                .description("item description")
                .available(false)
                .owner(UserMapper.mapToNewUser(userDto))
                .build();
        when(itemService.getItemById(userId, itemId))
                .thenReturn(itemDtoResponse);

        mvc.perform(get("/items/{itemId}", itemId)
                        .header(HEADER, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
    }

    @SneakyThrows
    @Test
    void postComment() {
        when(itemService.postComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);
        mvc.perform(post("/items/{itemId}/comment", itemId)
                        .content(mapper.writeValueAsString(commentDto))
                        .header(HEADER, 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }
}