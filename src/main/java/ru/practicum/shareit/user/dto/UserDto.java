package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;
    @NotNull(groups = {Create.class})
    private String name;
    @NotNull(groups = {Create.class})
    @Email(groups = {Update.class, Create.class})
    private String email;
}
