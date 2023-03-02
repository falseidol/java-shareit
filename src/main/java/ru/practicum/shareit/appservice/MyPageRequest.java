package ru.practicum.shareit.appservice;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.BadRequestException;


public class MyPageRequest {

    public static Pageable makePageRequest(Integer from, Integer size, Sort sort) {
        if (from == null || size == null)
            return null;

        if (from < 0 || size <= 0)
            throw new BadRequestException("Неправильно указанны параметры для просмотра!");
        return PageRequest.of(from / size, size, sort);
    }
}