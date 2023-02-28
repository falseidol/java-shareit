package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.appservice.MyPageRequest;
import ru.practicum.shareit.exception.BadRequestException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MyPageRequestTest {
    @Test
    void makePageable_whenInvoked_thenReturnPageable() {
        Integer from = 0;
        Integer size = 1;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        Pageable pageable = MyPageRequest.makePageRequest(from, size, sort);
        assertThat(pageable, notNullValue());
    }

    @Test
    void makePageable_whenFromIsNull_thenReturnNull() {
        Integer from = null;
        Integer size = 1;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        Pageable pageable = MyPageRequest.makePageRequest(from, size, sort);
        assertThat(pageable, nullValue());
    }

    @Test
    void makePageable_whenSizeIsZero_thenValidationExceptionThrown() {
        Integer from = 0;
        Integer size = 0;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        BadRequestException validationException = assertThrows(BadRequestException.class,
                () -> MyPageRequest.makePageRequest(from, size, sort));
        assertThat(validationException.getMessage(), equalTo("Неправильно указанны параметры для просмотра!"));
    }

    @Test
    void makePageable_whenFromIsNegative_thenValidationExceptionThrown() {
        Integer from = -2;
        Integer size = 4;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        BadRequestException validationException = assertThrows(BadRequestException.class,
                () -> MyPageRequest.makePageRequest(from, size, sort));
        assertThat(validationException.getMessage(), equalTo("Неправильно указанны параметры для просмотра!"));
    }
}
