package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

public interface RequestService {
    RequestDto saveRequest(Long userId, RequestDto requestDto);

    RequestDto getRequestById(Long userId,Long requestId);

    List<RequestDto> findAllByUserId(Long userId);

    List<RequestDto> findAllFromSize(Long userId,Integer from, Integer size);
}

