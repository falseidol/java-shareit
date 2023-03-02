package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findAllByUser_IdOrderByCreatedDesc(Long userId);

    List<Request> findAllByUser_IdNotIn(List<Long> userId, Pageable paging);
}