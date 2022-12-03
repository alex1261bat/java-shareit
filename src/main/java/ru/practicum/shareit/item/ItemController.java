package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public Item saveNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @Valid @RequestBody Item item) {
        return itemService.add(userId, item);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId,
                           @RequestBody Item item) {
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchAvailableItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam String text) {
        return itemService.searchAvailableItems(userId, text);
    }
}
