package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId, Pageable paging);

    @Query("SELECT i FROM Item i " +
            " WHERE i.available = TRUE " +
            "AND (LOWER(i.name) LIKE %?1% " +
            "OR LOWER(i.description) LIKE %?1%)")
    List<Item> searchByText(String text,Pageable paging);

    List<Item> findAllByRequestIdIn(List<Long> itemRequest);
    List<Item> findAllByRequestId(long itemRequest);
}