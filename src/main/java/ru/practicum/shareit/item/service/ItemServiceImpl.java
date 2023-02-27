package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.appservice.MyPageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.HeaderNotFoundException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        log.info("Добавление вещи");
        if (userId == null) {
            throw new HeaderNotFoundException("Заголовок не найден");
        }
        Item item = ItemMapper.fromDto(itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь не найден"));
        if (itemDto.getRequestId() != null) {
            Request request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ObjectNotFoundException("Реквест не найден"));
            item.setRequest(request);
        }
        //TODO требуется ли возврат
        item.setOwner(user);
        itemRepository.save(item);
        return ItemMapper.toDto(item);
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
        Item itemUpdate = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("итем с таким ид отсутствует"));
        if (itemUpdate.getOwner() != null && !Objects.equals(itemUpdate.getOwner().getId(), userId)) {
            throw new UserNotFoundException("У пользователя нет вещей или он не являвется владельцем");
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
        return ItemMapper.toDto(itemUpdate);
    }

    @Override
    public List<ItemDtoResponse> findAll(Long userId, Integer from, Integer size) {
        MyPageRequest pageRequest = new MyPageRequest(from,size, Sort.by(Sort.Direction.ASC, "id"));
        List<Item> items = itemRepository.findAllByOwnerId(userId,pageRequest);
        List<Booking> bookings = bookingRepository.findAllByItemOwnerId(userId);
        List<Comment> comments = commentRepository.findAllByItem_IdIn(items.stream()
                .map(Item::getId)
                .collect(Collectors.toList()));
        return items.stream()
                .map(item -> addBookingsAndComments(item, bookings, comments))
                .collect(Collectors.toList());
    }

    @Override
    public ItemDtoResponse getItemById(Long userId, Long itemId) {
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
        return itemDtoResponse;
    }

    @Override
    public List<ItemDto> searchItem(Integer from, Integer size, String text) {
        if (text == null) {
            throw new ObjectNotFoundException("searchText is null");
        } else if (text.isBlank()) {
            return Collections.emptyList();
        }
        MyPageRequest pageRequest = new MyPageRequest(from,size,Sort.by(Sort.Direction.ASC, "id"));
        text = text.toLowerCase();
        List<Item> itemList = itemRepository.searchByText(text,pageRequest);
        return itemList.stream().map(ItemMapper::toDto).collect(Collectors.toList());
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
            throw new BadRequestException("Нет прав оставлять комментарий!");

        Comment comment = CommentMapper.fromDto(commentDto, user, item, LocalDateTime.now());
        return CommentMapper.toDto(commentRepository.save(comment));
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
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
        BookingDtoShort nextBookingShort = BookingMapper.toDtoShort(nextBooking.orElse(null));
        BookingDtoShort lastBookingShort = BookingMapper.toDtoShort(lastBooking.orElse(null));
        return ItemMapper.toDtoResponse(item, nextBookingShort, lastBookingShort, itemComments);
    }
}