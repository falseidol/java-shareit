package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.HeaderNotFoundException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        log.info("Добавление вещи");
        if (userId == null) {
            throw new HeaderNotFoundException("Заголовок не найден");
        }
        Item item = ItemMapper.INSTANCE.fromDto(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        item.setOwner(user);
        itemRepository.save(item);
        return ItemMapper.INSTANCE.toDto(item);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId) {
        log.info("Обновление вещи");
        if (userId == null) {
            throw new ObjectNotFoundException("Не передан заголовок с ид пользователя");
        }
        if (itemId == null) {
            throw new ObjectNotFoundException("Не передан ид вещи");
        }
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("Такого пользователя нету");
        }
        Item itemUpdate = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("итем с таким ид отсутсвует"));
        if (itemUpdate.getOwner() != null && !Objects.equals(itemUpdate.getOwner().getId(), userId)) {
            throw new UserNotFoundException("У пользователя нет вещей");
        }
        if (itemDto.getName() != null) {
            itemUpdate.setName(itemDto.getName());
        }
        if (itemDto.getAvailable() != null) {
            itemUpdate.setAvailable(itemDto.getAvailable());
        }
        if (itemDto.getDescription() != null) {
            itemUpdate.setDescription(itemDto.getDescription());
        }
        itemRepository.save(itemUpdate);
        return ItemMapper.INSTANCE.toDto(itemUpdate);
    }

    @Override
    public List<ItemDtoResponse> findAll(Long userId) {
        List<Item> items = itemRepository.findAllByOwnerIdOrderById(userId);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(userId);
        List<Comment> comments = commentRepository.findAllByItem_IdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        return items.stream()
                .map(item -> addBookingsAndComments(item, bookings, comments))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("итем не найден"));
        log.info("Возвращаем вещь по ид");
        List<Booking> bookings = bookingRepository.findBookingByItem_Id(itemId);
        List<Comment> comments = commentRepository.findAllByItem_Id(itemId);
        ItemDtoResponse itemDtoResponse = addBookingsAndComments(item, bookings, comments);
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            itemDtoResponse.setLastBooking(null);
            itemDtoResponse.setNextBooking(null);
        }
        return ItemMapper.INSTANCE.toDto(item);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text == null) {
            throw new ObjectNotFoundException("searchText is null");
        }
        List<Item> itemList = itemRepository.searchItemsByNameContainingIgnoreCaseAndDescriptionIgnoreCaseAndAvailableIsTrue(text);
        return itemList.stream().map(ItemMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена"));
        log.info("Вещь по ид");
        return ItemMapper.INSTANCE.toDto(item);
    }

    @Transactional
    @Override
    public CommentDto postComment(Long userId, Long itemId, CommentDto commentDto) {
        final User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с таким id отсутствует!"));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException("Инструмент с таким id отсутствует!"));
        final Booking booking = bookingRepository.findFirstByItem_IdAndBooker_Id(itemId, userId)
                .orElseThrow(() -> new ObjectNotFoundException("Бронирование отсутствует!"));
        if (!booking.getStatus().equals(BookingStatus.APPROVED) || booking.getEnd().isAfter(LocalDateTime.now()))
            throw new RuntimeException("Нет прав оставлять комментарий!");

        Comment comment = CommentMapper.INSTANCE.fromDto(commentDto, user, item, LocalDateTime.now());
        return CommentMapper.INSTANCE.toDto(commentRepository.save(comment));
    }

    private static ItemDtoResponse addBookingsAndComments(Item item, List<Booking> bookings, List<Comment> comments) {
        Optional<Booking> nextBooking = bookings.stream()
                .filter(booking -> booking.getItem().getId().equals(item.getId()))
                .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                .min(Comparator.comparing(Booking::getEnd));
        Optional<Booking> lastBooking = bookings.stream()
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .filter(booking -> booking.getItem().getId().equals(item.getId()))
                .filter(booking -> booking.getEnd().isBefore(LocalDateTime.now()))
                .max(Comparator.comparing(Booking::getEnd));
        List<CommentDto> itemComments = comments.stream()
                .filter(comment -> comment.getItem().getId().equals(item.getId()))
                .map(CommentMapper.INSTANCE::toDto)
                .collect(Collectors.toList());
        BookingDtoShort nextBookingShort = BookingMapper.INSTANCE.toDtoShort(nextBooking.orElse(null));
        BookingDtoShort lastBookingShort = BookingMapper.INSTANCE.toDtoShort(lastBooking.orElse(null));
        return ItemMapper.INSTANCE.toDtoResponse(item, nextBookingShort, lastBookingShort, itemComments);
    }
}