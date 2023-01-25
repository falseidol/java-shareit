package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserRepository {

    void deleteById(Long id);

    Map<Long, User> getUserMap();

    User create(User user);

    Collection<User> findAll();

    User getUserFromMap(Long id);
}
