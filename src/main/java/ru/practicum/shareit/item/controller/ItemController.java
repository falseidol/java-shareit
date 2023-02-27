package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.appservice.Create;
import ru.practicum.shareit.appservice.Update;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.booking.controller.BookingController.HEADER;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader(HEADER) Long userId,
                           @Validated({Create.class}) @RequestBody ItemDto item) {
        return itemService.addItem(userId, item);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader(HEADER) Long userId, @Validated({Update.class}) @RequestBody ItemDto itemDto,
                              @PathVariable Long id) {
        return itemService.updateItem(userId, itemDto, id);
    }

    @GetMapping
    public Collection<ItemDtoResponse> findAll(@RequestHeader(HEADER) Long userId,
                                               @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        if (size == 0) {
            throw new BadRequestException("size == 0");
        }
        return itemService.findAll(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text,
                                    @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                    @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        if (size == 0) {
            throw new BadRequestException("size == 0");
        }
        return itemService.searchItem(from, size, text);
    }

    @GetMapping("/{id}")
    public ItemDtoResponse getItemByIdAndUserId(@RequestHeader(HEADER) Long userId, @PathVariable Long id) {
        return itemService.getItemById(userId, id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(HEADER) Long userId, @PathVariable Long itemId,
                                  @Validated({Create.class})
                                  @RequestBody CommentDto commentDto) {
        return itemService.postComment(userId, itemId, commentDto);
    }
}