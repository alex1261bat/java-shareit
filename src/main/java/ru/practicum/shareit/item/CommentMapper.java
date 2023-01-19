package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

public class CommentMapper {
    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(CommentRequestDto comment, Item item, User author) {
        return new Comment(
                comment.getId(),
                comment.getText(),
                item,
                author,
                comment.getCreated()
        );
    }
}
