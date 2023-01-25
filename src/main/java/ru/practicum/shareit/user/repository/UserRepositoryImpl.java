package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    @Override
    public Map<Long, User> getUserMap() {
        return userMap;
    }

    private final Map<Long, User> userMap = new HashMap<>();
    private static Long userId = 1L;

    @Override
    public User create(User user) {
        user.setId(userId++);
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return userMap.values();
    }

    @Override
    public User getUserFromMap(Long id) {
        return userMap.get(id);
    }

    @Override
    public void deleteById(Long id) {
        userMap.remove(id);
    }
}
