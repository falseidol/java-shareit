package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        log.info("Отправка списка пользов");
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Создание юзера");
        User user = userRepository.save(UserMapper.mapToNewUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        log.info("Обновление юзера");
        User savedUser = UserMapper.mapToNewUser(findById(userId));
        if (userDto.getEmail() != null) {
            savedUser.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            savedUser.setName(userDto.getName());
        }
        userRepository.save(savedUser);
        return UserMapper.toUserDto(savedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("Удаление юзера");
        userRepository.deleteById(userId);
    }

    @Transactional
    @Override
    public UserDto findById(Long userId) {
        return UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден")));
    }
}