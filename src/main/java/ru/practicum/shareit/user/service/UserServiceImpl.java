package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Collection<UserDto> findAll() {
        log.info("Отправка списка пользов");
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Создание юзера");
        validate(userDto);
        User user = userRepository.create(UserMapper.fromUserDtoToUser(userDto));
        return UserMapper.toUserDto(user);
    }

    private void validate(UserDto userDto) {
        if (userDto.getEmail() != null) {
            if (userRepository.findAll().stream().anyMatch(x -> x.getEmail().equals(userDto.getEmail()))) {
                throw new DuplicateEmailException("Duplicate email " + userDto.getEmail());
            }
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        log.info("Обновление юзера");
        if (!userRepository.getUserMap().containsKey(userId)) {
            throw new UserNotFoundException("User not found");
        }
        validate(userDto);
        User userUpdate = userRepository.getUserMap().get(userId);
        userDto.setId(userId);
        if (userDto.getEmail() != null) {
            userUpdate.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            userUpdate.setName(userDto.getName());
        }
        userRepository.getUserMap().put(userId, userUpdate);
        return UserMapper.toUserDto(userUpdate);
    }

    @Override
    public void deleteUser(Long userId) {
        log.info("Удаление юзера");
        if (!userRepository.getUserMap().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getById(Long userId) {
        if (!userRepository.getUserMap().containsKey(userId)) {
            throw new UserNotFoundException("Пользователь не найден");
        }
        return UserMapper.toUserDto(userRepository.getUserFromMap(userId));
    }
}
