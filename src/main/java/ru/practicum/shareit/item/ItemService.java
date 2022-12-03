package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    Item add(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);

    ItemDto getById(Long userId, Long itemId);

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> searchAvailableItems(Long userId, String text);
}
