package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.appservice.Create;
import ru.practicum.shareit.appservice.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotNull(groups = {Create.class})
    private String name;
    @NotNull(groups = {Create.class})
    @Email(groups = {Update.class, Create.class})
    private String email;
}