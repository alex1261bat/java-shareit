package ru.practicum.shareit.item;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.*;

@Component
@Slf4j
@Setter
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Map<Long, Item>> items = new HashMap<>();
    private long itemId = 0;

    @Override
    public Item add(Long userId, Item item) {
        item.setId(getId());
        items.compute(userId, (ownerId, userItems) -> {
            if (userItems == null) {
                userItems = new HashMap<>();
            }
            userItems.put(itemId, item);
            return userItems;
        });

        log.trace("Сохранен объект: {}", item);
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        Item storedItem = getItemByUserId(userId, itemId);

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
    public Item getById(Long userId, Long itemId) throws NotFoundException {
        Item item = null;

        for (Map<Long, Item> itemMap : items.values()) {
            item = itemMap.get(itemId);
        }

        if (item == null) {
            throw new NotFoundException("Вещь с id=" + itemId + " не существует");
        }

        return item;
    }

    @Override
    public List<Item> getUserItems(Long userId) {
        return new ArrayList<>(items.get(userId).values());
    }

    @Override
    public List<Item> searchAvailableItems(Long userId, String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> availableItems = new ArrayList<>();
        String editedText = text.toLowerCase();

        for (Map<Long, Item> itemList : items.values()) {
            for (Item item : itemList.values()) {
                if ((item.getName().toLowerCase().contains(editedText)
                        || item.getDescription().toLowerCase().contains(editedText))
                        && item.getAvailable()) {
                    availableItems.add(item);
                }
            }
        }

        return availableItems;
    }

    private long getId() {
        setItemId(itemId + 1);
        return itemId;
    }

    private Item getItemByUserId(Long userId, Long itemId) {
        Map<Long, Item> itemMap = items.get(userId);

        if (itemMap == null || !itemMap.containsKey(itemId)) {
            throw new NotFoundException("Вещь с id=" + itemId + " не принадлежит пользователю с id=" + userId);
        }

        return itemMap.get(itemId);
    }
}
