package ru.practicum.gateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.pageValidator.PageValidator;

import javax.validation.Valid;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> saveNewItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @Valid @RequestBody ItemDto itemDto) {
        log.info("Получен запрос на создание вещи от пользователя с id=" + userId);
        return itemClient.saveNewItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemDto itemDto,
                                             @PathVariable Long itemId) {
        log.info("Получен запрос на обновление вещи с id=" + itemId + " от пользователя с id=" + userId);
        return itemClient.updateItem(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @PathVariable Long itemId) {
        log.info("Получен запрос на получение вещи с id=" + itemId + " от пользователя с id=" + userId);
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(name = "from",
                                                                defaultValue = "0") Integer from,
                                                  @RequestParam(name = "size",
                                                                defaultValue = "10") Integer size) {
        PageValidator.validatePage(from, size);
        log.info("Получен запрос на получение вещей пользователя с id=" + userId + " с параметрами from=" + from
                + " size=" + size);
        return itemClient.getAllUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findAvailableItems(@RequestParam String text,
                                                     @RequestParam(name = "from",
                                                                   defaultValue = "0") Integer from,
                                                     @RequestParam(name = "size",
                                                                   defaultValue = "10") Integer size) {
        PageValidator.validatePage(from, size);
        log.info("Получен запрос на получение доступных вещей с параметрами текст поиска text=" + text + " from=" + from
                + " size=" + size);
        return itemClient.findAvailableItems(text.toLowerCase(),from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody CommentRequestDto commentRequestDto,
                                             @PathVariable Long itemId) {
        log.info("Получен запрос на добавление комментария к вещи с id=" + itemId + " от пользователя с id=" + userId);
        return itemClient.addComment(commentRequestDto, userId, itemId);
    }
}
