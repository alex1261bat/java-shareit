package ru.practicum.server.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.pageCreator.PageCreator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto saveNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @RequestBody ItemDto itemDto) {
        return itemService.saveNewItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemService.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDatesDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PathVariable Long itemId) {
        return itemService.getById(userId, itemId);
    }

    @GetMapping
    public List<ItemWithBookingDatesDto> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @RequestParam(name = "from",
                                                                       defaultValue = "0") Integer from,
                                                         @RequestParam(name = "size",
                                                                       defaultValue = "10") Integer size) {
        return itemService.getUserItems(userId, PageCreator.createPage(from, size));
    }

    @GetMapping("/search")
    public List<ItemDto> findAvailableItems(@RequestParam String text,
                                            @RequestParam(name = "from", defaultValue = "0") Integer from,
                                            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemService.findAvailableItems(text, PageCreator.createPage(from, size));
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId,
                                         @RequestBody CommentRequestDto commentRequestDto) {
        return itemService.addComment(userId, itemId, commentRequestDto);
    }
}
