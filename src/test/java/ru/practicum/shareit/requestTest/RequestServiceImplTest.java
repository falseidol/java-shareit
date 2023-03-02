package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Captor
    ArgumentCaptor<Request> requestArgumentCaptor;
    @InjectMocks
    private RequestServiceImpl requestService;
    private Request itemRequest;
    private RequestDto itemRequestDto;
    private Item item;
    private User user;
    private Request itemRequest1;
    private Request itemRequest2;
    private Item item1;
    private Item item2;
    private User user3;

    private final long userId = 1L;
    private final int from = 0;
    private final int size = 5;

    @BeforeEach
    void setUp() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("userName")
                .email("name@mail.com")
                .build();
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item name")
                .description("item description")
                .available(false)
                .build();
        user = UserMapper.mapToNewUser(userDto);
        item = ItemMapper.fromDto(itemDto);
        itemRequest = Request.builder()
                .id(1L)
                .description("request description")
                .build();
        itemRequestDto = RequestDto.builder()
                .id(1L)
                .description("itemRequest description")
                .created(LocalDateTime.now().plusDays(1))
                .build();
        itemRequest1 = Request.builder()
                .id(10L)
                .user(user)
                .build();
        itemRequest2 = Request.builder()
                .id(20L)
                .user(user)
                .build();
        user3 = User.builder()
                .id(3L)
                .name("userName")
                .email("name@mail.com")
                .build();
    }

    @Test
    void saveRequestTest() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        requestService.saveRequest(userId, itemRequestDto);
        verify(requestRepository).save(requestArgumentCaptor.capture());
        Request value = requestArgumentCaptor.getValue();
        assertThat(value.getUser(), equalTo(user));
    }

    @Test
    void saveRequestTestUserNotFoundException() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> requestService.saveRequest(userId, itemRequestDto));
        assertThat(exception.getMessage(), equalTo("Юзер не найден"));
    }

    @Test
    void findAllByUserId() {
        long userId = 1L;
        itemRequest1 = Request.builder()
                .id(10L)
                .user(user)
                .build();
        itemRequest2 = Request.builder()
                .id(20L)
                .user(user)
                .build();
        user3 = User.builder()
                .id(3L)
                .name("userName")
                .email("name@mail.com")
                .build();
        item1 = Item.builder()
                .id(2L)
                .owner(user3)
                .request(itemRequest1)
                .build();
        item2 = Item.builder()
                .id(3L)
                .owner(user3)
                .request(itemRequest2)
                .build();
        item.setOwner(user3);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByUser_IdOrderByCreatedDesc(userId))
                .thenReturn(Arrays.asList(itemRequest1, itemRequest2));
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(Arrays.asList(item1, item2));
        List<RequestDto> allByRequestorId = requestService.findAllByUserId(userId);
        assertThat(allByRequestorId, hasSize(2));
        assertThat(allByRequestorId.get(0).getId(), equalTo(10L));
        assertThat(allByRequestorId.get(1).getId(), equalTo(20L));
    }

    @Test
    void findAllByUserIdTestUserNotFoundException() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> requestService.findAllByUserId(userId));
        assertThat(exception.getMessage(), equalTo("Юзер не найден"));
    }

    @Test
    void findAllFromSizeTest() {
        item.setOwner(user3);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByUser_IdNotIn(anyList(), any(Pageable.class)))
                .thenReturn(Collections.emptyList());
        when(itemRepository.findAllByRequestIdIn(anyList()))
                .thenReturn(Arrays.asList(item1, item2));
        List<RequestDto> allByParams = requestService.findAllFromSize(userId, from, size);
        assertThat(allByParams, hasSize(0));
    }

    @Test
    void findAllFromSizeUserNotFoundException() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> requestService.findAllFromSize(userId, from, size));
        assertThat(exception.getMessage(), equalTo("Юзер не найден"));
    }

    @Test
    void getRequestByIdTest() {
        long requestId = 1L;
        when(requestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequestId(requestId))
                .thenReturn(Collections.singletonList(item));
        RequestDto result = requestService.getRequestById(userId, requestId);
        assertThat(result.getItems(), hasSize(1));
    }

    @Test
    void getRequestByIdUserNotFoundException() {
        long requestId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> requestService.getRequestById(userId, requestId));
        assertThat(exception.getMessage(), equalTo("Юзер не найден."));
    }

    @Test
    void getRequestByIdObjectNotFoundException() {
        long requestId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(requestRepository.findById(requestId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> requestService.getRequestById(userId, requestId));
        assertThat(exception.getMessage(), equalTo("Реквест не найден."));
    }
}