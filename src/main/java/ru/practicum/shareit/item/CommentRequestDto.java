package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    private Long id;
    @NotBlank
    private String text;
    private Long itemId;
    private Long authorId;
    private LocalDateTime created;
}
