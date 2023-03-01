package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
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
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    private ArgumentCaptor<Comment> commentArgumentCaptor;
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    private ItemDto itemDto;
    private UserDto userDto;
    private CommentDto commentDto;
    private Request itemRequest;
    private Item item;
    private User user;
    private Comment comment;
    private Booking booking1;
    private Booking booking2;
    private final long userId = 1L;
    private final long requestId = 1L;
    private final long itemId = 1L;

    @BeforeEach
    void setUp() {
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
        user = UserMapper.mapToNewUser(userDto);
        item = ItemMapper.fromDto(itemDto);
        commentDto = CommentDto.builder()
                .id(1L)
                .text("comment text")
                .authorName("antony")
                .build();
        itemRequest = Request.builder()
                .id(1L)
                .description("request description")
                .build();
        comment = Comment.builder()
                .id(1L)
                .text("asdasd")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();
        booking1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        booking2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void addItemSuccess() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        itemService.addItem(userId, itemDto);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item actualItem = itemArgumentCaptor.getValue();
        assertThat(actualItem.getOwner().getName(), equalTo("userName"));
    }

    @Test
    void addItemWhenUserNotFound() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.addItem(userId, itemDto));
        assertThat(exception.getMessage(), equalTo("Пользователь не найден"));
    }

    @Test
    void addItemWithRequestNotNull() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        itemDto.setRequestId(requestId);
        when(requestRepository.findById(itemDto.getRequestId()))
                .thenReturn(Optional.of(itemRequest));
        itemService.addItem(userId, itemDto);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item actualItem = itemArgumentCaptor.getValue();
        assertThat(actualItem.getRequest(), equalTo(itemRequest));
    }

    @Test
    void addItemWithRequestNull() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        itemDto.setRequestId(requestId);
        when(requestRepository.findById(itemDto.getRequestId()))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () -> itemService.addItem(userId, itemDto));
        assertThat(exception.getMessage(), equalTo("Реквест не найден"));
    }

    @Test
    void getByIdItemFoundSuccess() {
        itemDto.setOwner(user);
        booking1.setEnd(LocalDateTime.now());
        booking1.setStart(LocalDateTime.now().plusDays(1L));
        booking2.setEnd(LocalDateTime.now());
        booking2.setStart(LocalDateTime.now().plusDays(1L));
        Item item = ItemMapper.fromDto(itemDto);
        when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItem_Id(userId))
                .thenReturn(Arrays.asList(booking1, booking2));
        when(commentRepository.findAllByItem_Id(userId))
                .thenReturn(Collections.singletonList(comment));
        ItemDtoResponse itemDtoResponse = itemService.getItemById(userId, itemId);
        assertThat(itemDtoResponse.getNextBooking().getId(), equalTo(1L));
        assertThat(itemDtoResponse.getLastBooking(), nullValue());
    }

    @Test
    void getByIdIfVerificationFailureBookingsNull() {
        User user2 = UserMapper.mapToNewUser(userDto);
        user2.setId(2L);
        itemDto.setOwner(user2);
        Item item = ItemMapper.fromDto(itemDto);
        when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItem_Id(userId))
                .thenReturn(Arrays.asList(booking1, booking2));
        when(commentRepository.findAllByItem_Id(userId))
                .thenReturn(Collections.singletonList(comment));
        ItemDtoResponse itemDtoResponse = itemService.getItemById(userId, itemId);
        assertThat(itemDtoResponse.getLastBooking(), nullValue());
        assertThat(itemDtoResponse.getNextBooking(), nullValue());
    }

    @Test
    void getByIdItemNotFound() {
        itemDto.setOwner(user);
        when(itemRepository.findById(userId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.getItemById(userId, itemId));
        assertThat(exception.getMessage(), equalTo("итем не найден"));
    }

    @Test
    void findAll() {
        long ownerId = 1L;
        int from = 0;
        int size = 5;
        when(itemRepository.findAllByOwnerId(anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(Collections.singletonList(item));
        when(bookingRepository.findAllByItemOwnerId(ownerId))
                .thenReturn(Collections.singletonList(booking1));
        when(commentRepository.findAllByItem_IdIn(Mockito.any()))
                .thenReturn(Collections.singletonList(comment));

        List<ItemDtoResponse> returnedList = itemService.findAll(ownerId, from, size);

        assertThat(returnedList, hasSize(1));
        assertThat(returnedList.get(0).getId(), is(1L));
    }

    @Test
    void updateItemNotFoundShouldThrowException() {
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.updateItem(userId, itemDto, itemId));
        assertThat(exception.getMessage(), equalTo("итем с таким ид отсутствует"));
    }

    @Test
    void updateIfUserNotOwnerThenThrowException() {
        long wrongId = 10L;
        item.setOwner(user);
        ItemDto itemToUpdate = ItemDto.builder()
                .name("UpdatedName")
                .description("UpdatedDescription")
                .owner(user)
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.updateItem(wrongId, itemToUpdate, 1L));
        assertThat(exception.getMessage(), equalTo("У пользователя нет вещей или он не являвется владельцем"));
    }

    @Test
    void updateItemTest() {
        itemDto.setName("bebrik");
        itemDto.setDescription("description");
        itemDto.setOwner(user);
        ItemDto itemToUpdate = ItemDto.builder()
                .name("UpdatedName")
                .description("UpdatedDescription")
                .owner(user)
                .available(true)
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        itemService.updateItem(userId, itemToUpdate, itemId);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item updatedItem = itemArgumentCaptor.getValue();
        assertThat(updatedItem.getName(), equalTo(itemToUpdate.getName()));
        assertThat(updatedItem.getDescription(), equalTo(itemToUpdate.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(itemToUpdate.getAvailable()));
    }

    @Test
    void updateItemUserIdWrong() {
        item.setOwner(user);
        user.setId(19L);
        ItemDto itemToUpdate = ItemDto.builder()
                .name("UpdatedName")
                .description("UpdatedDescription")
                .owner(user)
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.updateItem(2L, itemToUpdate, 1L));
        assertThat(exception.getMessage(), equalTo("У пользователя нет вещей или он не являвется владельцем"));
    }

    @Test
    void updateItemTest2() {
        itemDto.setName("bebrik");
        itemDto.setDescription("description");
        itemDto.setOwner(user);
        ItemDto itemToUpdate = ItemDto.builder()
                .name(null)
                .description(null)
                .owner(user)
                .available(null)
                .build();
        item.setOwner(null);
        item.setName(null);
        item.setDescription(null);
        item.setAvailable(null);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        itemService.updateItem(userId, itemToUpdate, itemId);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item updatedItem = itemArgumentCaptor.getValue();
        assertThat(updatedItem.getName(), equalTo(itemToUpdate.getName()));
        assertThat(updatedItem.getDescription(), equalTo(itemToUpdate.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(itemToUpdate.getAvailable()));
    }

    @Test
    void updateItemUserNull() {
        ItemDto itemToUpdate = ItemDto.builder()
                .id(1L)
                .name("UpdatedName")
                .description("UpdatedDescription")
                .owner(user)
                .build();
        item.setOwner(user);
        item.getOwner().setId(15L);
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.updateItem(10L, itemToUpdate, 1L));
        assertThat(exception.getMessage(), equalTo("У пользователя нет вещей или он не являвется владельцем"));
    }

    @Test
    void updateItemUserIsNull() {
        itemDto.setOwner(null);
        ItemDto itemToUpdate = ItemDto.builder()
                .name("UpdatedName")
                .description("UpdatedDescription")
                .owner(user)
                .available(true)
                .build();
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        itemService.updateItem(userId, itemToUpdate, itemId);
        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item updatedItem = itemArgumentCaptor.getValue();
        assertThat(updatedItem.getName(), equalTo("UpdatedName"));
        assertThat(updatedItem.getDescription(), equalTo("UpdatedDescription"));
        assertTrue(updatedItem.getAvailable());
    }

    @Test
    void searchItem() {
        String text = "aSd";
        int from = 0;
        int size = 5;
        when(itemRepository.searchByText(anyString(), Mockito.any(Pageable.class)))
                .thenReturn(Collections.singletonList(item));
        List<ItemDto> searchResults = itemService.searchItem(from, size, text);
        assertThat(searchResults, hasSize(1));
    }

    @Test
    void searchItemTextIsNull() {
        String text = "";
        int from = 0;
        int size = 5;
        List<ItemDto> itemDtos = Collections.emptyList();
        assertEquals(itemService.searchItem(from, size, text), itemDtos);
    }

    @Test
    void postComment() {
        comment.setText("testText");
        commentDto = CommentMapper.toDto(comment);
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setEnd(LocalDateTime.now());
        booking2.setStart(LocalDateTime.now().plusDays(3));
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItem_IdAndBooker_Id(itemId, userId))
                .thenReturn(Optional.of(booking2));
        itemService.postComment(userId, itemId, commentDto);
        verify(commentRepository).save(commentArgumentCaptor.capture());
        Comment testComment = commentArgumentCaptor.getValue();
        assertThat(testComment.getText(), equalTo("testText"));
    }

    @Test
    void postCommentWithWrongStatusShouldThrowException() {
        commentDto.setText("testText");
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItem_IdAndBooker_Id(itemId, userId))
                .thenReturn(Optional.of(booking2));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.postComment(userId, itemId, commentDto));
        assertThat(exception.getMessage(), equalTo("Нет прав оставлять комментарий!"));
    }

    @Test
    void postCommentWithWrongStartTimeShouldThrowException() {
        booking2.setStatus(BookingStatus.APPROVED);
        booking2.setStart(LocalDateTime.now());
        booking2.setEnd(LocalDateTime.now().plusDays(3));
        commentDto.setText("testText");
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItem_IdAndBooker_Id(itemId, userId))
                .thenReturn(Optional.of(booking2));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> itemService.postComment(userId, itemId, commentDto));
        assertThat(exception.getMessage(), equalTo("Нет прав оставлять комментарий!"));
    }

    @Test
    void postCommentWhenUserNotFoundShouldThrowException() {
        commentDto.setText("testText");
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> itemService.postComment(userId, itemId, commentDto));
        assertThat(exception.getMessage(), equalTo("Пользователь с таким id отсутствует!"));
    }

    @Test
    void postCommentWhenBookingNotFoundShouldThrowException() {
        commentDto.setText("testText");
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findFirstByItem_IdAndBooker_Id(itemId, userId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.postComment(userId, itemId, commentDto));
        assertThat(exception.getMessage(), equalTo("Бронирование отсутствует!"));
    }

    @Test
    void postCommentWhenItemNotFoundShouldThrowException() {
        long userId = 1L;
        long itemId = 1L;
        commentDto.setText("testText");
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> itemService.postComment(userId, itemId, commentDto));
        assertThat(exception.getMessage(), equalTo("Инструмент с таким id отсутствует!"));
    }
}