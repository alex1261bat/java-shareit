package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

public interface ItemStorage {
    Item add(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);

    ItemDto getById(Long userId, Long itemId) throws NotFoundException;

    List<ItemDto> getUserItems(Long userId);

    List<ItemDto> searchAvailableItems(Long userId, String text);
}
