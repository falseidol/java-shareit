package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.enums.BookingStatus.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public BookingDto addBooking(Long userId, BookingDtoCreate bookingDtoCreate) {
        log.info("добавление аренды");
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDtoCreate.getItemId()).orElseThrow(() -> new ObjectNotFoundException("Итем не найден"));

        if (!item.getAvailable()) {
            throw new BadRequestException("Предмет не доступен для бронирования");
        }
        if (bookingDtoCreate.getEnd().isBefore(bookingDtoCreate.getStart())) {
            throw new BadRequestException("Неправильно указана дата,конец перед стартом");
        }
        if (Objects.equals(item.getOwner().getId(), userId))
            throw new ObjectNotFoundException("Нет подходящих для бронирования предметов!");
        Booking booking = Booking.builder()
                .start(bookingDtoCreate.getStart())
                .end(bookingDtoCreate.getEnd())
                .item(item)
                .booker(user)
                .status(WAITING)
                .build();
        return BookingMapper.toDtoResponse(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, boolean approved) {
        log.info("подтверждение аренды");
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Аренда не найдена"));
        final Item item = booking.getItem();

        if (!item.getOwner().getId().equals(userId)) {
            throw new ObjectNotFoundException("Пользователь не является владельцем аренды");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new BadRequestException("Изменение статуса недоступно");
        }
        if (approved)
            booking.setStatus(APPROVED);
        else
            booking.setStatus(REJECTED);
        return BookingMapper.toDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingByBookingId(Long userId, Long bookingId) {
        log.info("Возвращаем аренду автору или владельцу");
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException("Аренда не найдена"));

        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId)
            return BookingMapper.toDtoResponse(booking);
        else
            throw new ObjectNotFoundException("Вы не являеетесь владельцем запроса");
    }

    @Override
    public List<BookingDto> getBookingsForDefaultUser(Long userId, String state) {
        List<Booking> bookingList;
        BookingStatus status;
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не существует!"));
        try {
            status = BookingStatus.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state); // TODO RUNTIME ВМЕСТО BADREQUEST
        }
        switch (status) {
            case ALL:
                bookingList = bookingRepository.findBookingByBooker_IdOrderByIdDesc(userId);
                break;
            case CURRENT:
                bookingList = bookingRepository.findBookingByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookingList = bookingRepository.findBookingByBookerIdAndEndIsBeforeAndStatusIs(userId, LocalDateTime.now(), APPROVED);
                break;
            case FUTURE:
                bookingList = bookingRepository.findBookingByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findBookingByBookerIdAndStatus(userId, WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findBookingByBookerIdAndStatus(userId, REJECTED);
                break;
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
        return bookingList.stream().map(BookingMapper::toDtoResponse).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findBookingsByOwner(Long userId, String state) {
        List<Booking> bookingList = new ArrayList<>();
        BookingStatus status;
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не существует!"));
        try {
            status = BookingStatus.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + state);
        }
        switch (status) {
            case ALL:
                bookingList = bookingRepository.findBookingByItemOwnerIdOrderByIdDesc(userId);
                break;
            case CURRENT:
                bookingList = bookingRepository
                        .findBookingByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                                LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookingList = bookingRepository.findBookingByItemOwnerIdAndEndIsBeforeAndStatusIs(userId,
                        LocalDateTime.now(), APPROVED);
                break;
            case FUTURE:
                bookingList = bookingRepository.findBookingByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookingList = bookingRepository.findBookingByItemOwnerIdAndStatus(userId, WAITING);
                break;
            case REJECTED:
                bookingList = bookingRepository.findBookingByItemOwnerIdAndStatus(userId, REJECTED);
                break;
        }
        return bookingList.stream()
                .map(BookingMapper::toDtoResponse)
                .collect(Collectors.toList());
    }
}