package ru.practicum.shareit.item;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@Setter
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private long itemId = 0;

    @Override
    public Item add(Long userId, Item item) {
        item.setId(getId());
        items.compute(userId, (ownerId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });

        log.trace("Сохранен объект: {}", item);
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        getById(userId, itemId);
        Item storedItem = findItemById(userId, itemId);

        if (item.getName() != null) {
            storedItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            storedItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            storedItem.setAvailable(item.getAvailable());
        }

        log.trace("Обновлен объект: {}", storedItem);
        return storedItem;
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) throws NotFoundException {
        Item item = null;

        if (items.get(userId) == null) {
            throw new NotFoundException("Пользователь с id=" + userId + " не существует");
        }

        for (Item element : items.get(userId)) {
            if (element.getId() == itemId) {
                item = element;
            }
        }

        if (item == null) {
            throw new NotFoundException("Вещь с id=" + itemId + " не существует");
        }

        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        List<Item> userItems = items.getOrDefault(userId, Collections.emptyList());

        return userItems.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(Long userId, String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        List<ItemDto> availableItems = new ArrayList<>();

        for (List<Item> itemList : items.values()) {
            for (Item item : itemList) {
                if ((item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && item.getAvailable()) {
                    availableItems.add(ItemMapper.toItemDto(item));
                }
            }
        }

        return availableItems;
    }

    private long getId() {
        setItemId(itemId + 1);
        return itemId;
    }

    private Item findItemById(Long userId, Long itemId) {
        List<Item> itemList = items.get(userId);
        Item item = null;

        for (Item element : itemList) {
            if (element.getId() == itemId) {
                item = element;
            }
        }
        return item;
    }
}
