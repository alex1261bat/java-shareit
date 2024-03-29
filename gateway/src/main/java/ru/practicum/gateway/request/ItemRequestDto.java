package ru.practicum.gateway.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {

    private Long id;
    @NotBlank
    private String description;
    private LocalDateTime created;
    private Long requestorId;
}
