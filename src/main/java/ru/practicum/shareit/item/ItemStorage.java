package ru.practicum.shareit.item;

import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

public interface ItemStorage {
    Item add(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);

    Item getById(Long userId, Long itemId) throws NotFoundException;

    List<Item> getUserItems(Long userId);

    List<Item> searchAvailableItems(Long userId, String text);
}
