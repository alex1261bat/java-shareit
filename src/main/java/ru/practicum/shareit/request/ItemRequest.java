package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequest {
    private final long id;
    @NotBlank
    private String description;
    @Positive
    private long requestor;
    @FutureOrPresent
    private LocalDateTime created;
}
