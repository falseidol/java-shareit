package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.appservice.Create;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static ru.practicum.shareit.booking.controller.BookingController.HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class RequestController {
    private final RequestService requestService;

    @PostMapping
    public RequestDto createRequest(@RequestHeader(HEADER) Long userId, @Validated({Create.class}) @RequestBody RequestDto requestDto) {
        return requestService.saveRequest(userId, requestDto);
    }

    @GetMapping("/all")
    public List<RequestDto> findAllFromSize(@RequestHeader(HEADER) Long userId,
                                            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return requestService.findAllFromSize(userId, from, size);
    }

    @GetMapping
    public List<RequestDto> findAllByUserId(@RequestHeader(HEADER) Long userId) {
        return requestService.findAllByUserId(userId);
    }

    @GetMapping("/{requestId}")
    public RequestDto getById(@RequestHeader(HEADER) Long userId, @PathVariable Long requestId) {
        return requestService.getRequestById(userId, requestId);
    }
}