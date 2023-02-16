package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.user.dto.Create;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@Data
@Builder
public class ItemDto {
    private Long id;
    @NotNull(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String name;
    @NotBlank(groups = {Create.class})
    @NotNull(groups = {Create.class})
    private String description;
    @NotNull(groups = {Create.class})
    private Boolean available;
    private User owner;
}