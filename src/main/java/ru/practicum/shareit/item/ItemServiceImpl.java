package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public Item add(Long userId, Item item) {
        userStorage.getById(userId);
        return itemStorage.add(userId, item);
    }

    @Override
    public Item update(Long userId, Long itemId, Item item) {
        return itemStorage.update(userId, itemId, item);
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        return itemStorage.getById(userId, itemId);
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemStorage.getUserItems(userId);
    }

    @Override
    public List<ItemDto> searchAvailableItems(Long userId, String text) {
        return itemStorage.searchAvailableItems(userId, text);
    }
}
