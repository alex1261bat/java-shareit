package ru.practicum.server.item;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemService {
    ItemDto saveNewItem(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemDtoId, ItemDto itemDto);

    ItemWithBookingDatesDto getById(Long userId, Long itemId);

    List<ItemWithBookingDatesDto> getUserItems(Long userId, Pageable pageRequest);

    List<ItemDto> findAvailableItems(String text, Pageable pageRequest);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
