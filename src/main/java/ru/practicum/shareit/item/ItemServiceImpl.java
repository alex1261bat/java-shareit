package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        userStorage.getById(userId);
        return ItemMapper.toItemDto(itemStorage.add(userId, ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        return ItemMapper.toItemDto(itemStorage.update(userId, itemId, ItemMapper.toItem(itemDto)));
    }

    @Override
    public ItemDto getById(Long userId, Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getById(userId, itemId));
    }

    @Override
    public List<ItemDto> getUserItems(Long userId) {
        return itemStorage.getUserItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(Long userId, String text) {
        return itemStorage.searchAvailableItems(userId, text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
