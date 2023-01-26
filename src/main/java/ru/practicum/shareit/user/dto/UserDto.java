package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserDto {
    private Long id;
    @NotNull(groups = {Create.class})
    private String name;
    @NotNull(groups = {Create.class})
    @Email(groups = {Update.class, Create.class})
    private String email;
}
