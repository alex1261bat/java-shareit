package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class ItemDto {
    private final long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private boolean available;
    @Positive
    private long owner;
    @Positive
    private long request;
}
