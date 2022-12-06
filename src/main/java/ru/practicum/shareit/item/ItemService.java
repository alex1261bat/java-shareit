package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    ItemDto add(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemDtoId, ItemDto itemDto);

    ItemDto getById(Long userId, Long itemId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> searchAvailableItems(Long userId, String text);
}
