package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto saveNewItem(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemDtoId, ItemDto itemDto);

    ItemWithBookingDatesDto getById(Long userId, Long itemId);

    List<ItemWithBookingDatesDto> getUserItems(Long userId);

    List<ItemDto> findAvailableItems(String text);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
