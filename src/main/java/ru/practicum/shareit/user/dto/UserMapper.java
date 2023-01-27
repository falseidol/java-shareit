package ru.practicum.shareit.user.dto;

import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.model.User;

public final class UserMapper {
    public static UserDto toUserDto(User user) {
        if (user == null) {
            throw new ObjectNotFoundException("object==null");
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email((user.getEmail()))
                .build();
    }

    public static User fromUserDtoToUser(UserDto userDto) {
        if (userDto == null) {
            throw new ObjectNotFoundException("object==null");
        }
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email((userDto.getEmail()))
                .build();
    }
}
