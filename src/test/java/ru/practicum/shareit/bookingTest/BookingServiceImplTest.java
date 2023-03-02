package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;
    ItemDto itemDto;
    UserDto userDto;
    CommentDto commentDto;
    Request itemRequest;
    Item item;
    User user;
    Comment comment;
    Booking booking;
    BookingDto bookingDto;
    private final Long userId = 1L;
    private final Long bookingId = 1L;
    private Long ownerId = 1L;
    private final int from = 0;
    private final int size = 4;

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
                .authorName("Vasya")
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
        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        bookingDto = BookingDto.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void addBookingTest() {
        user.setId(2L);
        item.setAvailable(true);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));
        bookingService.addBooking(userId, bookingDtoCreate);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertThat(savedBooking.getStart(), equalTo(bookingDtoCreate.getStart()));
        assertThat(savedBooking.getEnd(), equalTo(bookingDtoCreate.getEnd()));
        assertThat(savedBooking.getBooker(), equalTo(user));
        assertThat(savedBooking.getItem(), equalTo(item));
        assertThat(savedBooking.getStatus(), is(BookingStatus.WAITING));
    }

    @Test
    void addBookingUserNotFoundThrowException() {
        user.setId(2L);
        item.setAvailable(true);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> bookingService.addBooking(userId, bookingDtoCreate));
        assertThat(exception.getMessage(), equalTo("Пользователь не найден"));
    }

    @Test
    void addBookingItemNotFoundThrowException() {
        user.setId(2L);
        item.setAvailable(true);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(userId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.addBooking(userId, bookingDtoCreate));
        assertThat(exception.getMessage(), equalTo("Итем не найден"));
    }

    @Test
    void addBookingItemNotAvailableThrowException() {
        user.setId(2L);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.addBooking(userId, bookingDtoCreate));
        assertThat(exception.getMessage(), equalTo("Предмет не доступен для бронирования"));
    }

    @Test
    void addBookingWrongTimeThrowException() {
        user.setId(2L);
        item.setAvailable(true);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.addBooking(userId, bookingDtoCreate));
        assertThat(exception.getMessage(), equalTo("Неправильно указана дата,конец перед стартом"));
    }

    @Test
    void addBookingItemAndBookingSameThrowException() {
        user.setId(1L);
        item.setAvailable(true);
        item.setOwner(user);
        BookingDtoCreate bookingDtoCreate = BookingDtoCreate.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(1L)
                .build();
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(userId))
                .thenReturn(Optional.of(item));

        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.addBooking(userId, bookingDtoCreate));
        assertThat(exception.getMessage(), equalTo("Нет подходящих для бронирования предметов!"));
    }

    @Test
    void bookingApprove_whenInvoked_thenBookingStatusChanged() {
        boolean approved = true;
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        bookingService.updateBooking(ownerId, bookingId, approved);
        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        Booking savedBooking = bookingArgumentCaptor.getValue();
        assertThat(savedBooking.getBooker(), equalTo(user));
        assertThat(savedBooking.getItem(), equalTo(item));
        assertThat(savedBooking.getStatus(), is(BookingStatus.APPROVED));
    }

    @Test
    void bookingUpdateBookingNotFoundThrowException() {
        boolean approved = true;
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateBooking(ownerId, bookingId, approved));
        assertThat(exception.getMessage(), equalTo("Аренда не найдена"));
    }

    @Test
    void bookingUpdateItemOwnerNotFoundExceptionThrow() {
        ownerId = 2L;
        boolean approved = true;
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.updateBooking(ownerId, bookingId, approved));
        assertThat(exception.getMessage(), equalTo("Пользователь не является владельцем аренды"));
    }

    @Test
    void bookingUpdateWrongStatusThrowException() {
        boolean approved = true;
        booking.setStatus(BookingStatus.REJECTED);
        item.setOwner(user);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.updateBooking(ownerId, bookingId, approved));
        assertThat(exception.getMessage(), equalTo("Изменение статуса недоступно"));
    }

    @Test
    void getBookingByBookingIdAndUserId() {
        User owner = User.builder()
                .id(3L)
                .build();
        item.setOwner(owner);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        BookingDto bookingReturned = bookingService.getBookingByBookingId(userId, bookingId);
        assertThat(bookingReturned.getId(), equalTo(1L));
        assertThat(bookingReturned.getBooker(), equalTo(user));
    }

    @Test
    void getBookingByBookingIdNotFoundThrowException() {
        User owner = User.builder()
                .id(3L)
                .build();
        item.setOwner(owner);
        booking.setItem(item);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingByBookingId(userId, bookingId));
        assertThat(exception.getMessage(), equalTo("Аренда не найдена"));
    }

    @Test
    void getBookingByBookingIdWrongUserThrowException() {
        User owner = User.builder()
                .id(3L)
                .build();
        item.setOwner(owner);
        booking.setItem(item);
        User user2 = User.builder()
                .id(4L)
                .build();
        booking.setBooker(user2);
        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.of(booking));
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingByBookingId(userId, bookingId));
        assertThat(exception.getMessage(), equalTo("Вы не являеетесь владельцем запроса"));
    }

    @Test
    void getBookingsForDefaultUserByParamStateALL() {
        String state = "ALL";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBooker_IdOrderByIdDesc(anyLong(), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.getBookingsForDefaultUser(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void getBookingsForDefaultUserByParamNotFoundThrowException() {
        String state = "ALL";
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingsForDefaultUser(userId, state, from, size));
        assertThat(exception.getMessage(), equalTo("Пользователь не существует!"));
    }

    @Test
    void getBookingsForDefaultUserByParamWrongStateThrowException() {
        String state = "WrongState";
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.getBookingsForDefaultUser(userId, state, from, size));
        assertThat(exception.getMessage(), equalTo("Unknown state: " + state));
    }

    @Test
    void getBookingsForDefaultUserByParamStateCURRENT() {
        String state = "current";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.getBookingsForDefaultUser(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void getBookingsForDefaultUserByParamStatePAST() {
        String state = "past";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndEndIsBeforeAndStatusIs(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.getBookingsForDefaultUser(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void getBookingsForDefaultUserByParamStateFUTURE() {
        String state = "future";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.getBookingsForDefaultUser(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void getAllBookingsForDefaultUserByParamStateWAITING() {
        String state = "waiting";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.getBookingsForDefaultUser(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void getAllBookingsForDefaultUserByParamStateREJECTED() {
        String state = "rejected";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByBookerIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.getBookingsForDefaultUser(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findBookingsByOwnerStateALL() {
        String state = "ALL";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdOrderByIdDesc(anyLong(), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.findBookingsByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findBookingsByOwnerUserNotFoundThrowException() {
        String state = "ALL";
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.findBookingsByOwner(userId, state, from, size));
        assertThat(exception.getMessage(), equalTo("Пользователь не существует!"));
    }

    @Test
    void findBookingsByOwnerWrongStateThrowException() {
        String state = "WrongState";
        when(userRepository.findById(userId))
                .thenReturn(Optional.ofNullable(user));
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> bookingService.findBookingsByOwner(userId, state, from, size));
        assertThat(exception.getMessage(), equalTo("Unknown state: " + state));
    }

    @Test
    void findBookingsByOwnerStateCURRENT() {
        String state = "current";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.findBookingsByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findBookingsByOwnerStatePAST() {
        String state = "past";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdAndEndIsBeforeAndStatusIs(anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.findBookingsByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findBookingsByOwnerStateFUTURE() {
        String state = "future";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.findBookingsByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findBookingsByOwnerStateWAITING() {
        String state = "waiting";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.findBookingsByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }

    @Test
    void findBookingsByOwnerStateREJECTED() {
        String state = "rejected";
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findBookingByItemOwnerIdAndStatus(anyLong(),
                any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(Collections.singletonList(booking));
        List<BookingDto> returnedList = bookingService.findBookingsByOwner(userId, state, from, size);
        assertThat(returnedList, hasSize(1));
    }
}