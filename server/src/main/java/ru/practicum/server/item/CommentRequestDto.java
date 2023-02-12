package ru.practicum.server.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    private Long id;
    private String text;
    private Long itemId;
    private Long authorId;
    private LocalDateTime created;
}
