package ru.practicum.gateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.pageValidator.PageValidator;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> saveNewItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Получен запрос на создание запроса вещи от пользователя с id=" + userId);
        return itemRequestClient.saveNewItemRequest(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Получен запрос на получение всех запросов вещей пользователя с id=" + userId);
        return itemRequestClient.getAllByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(name = "from",
                                                       defaultValue = "0") Integer from,
                                         @RequestParam(name = "size",
                                                       defaultValue = "10") Integer size) {
        PageValidator.validatePage(from, size);
        log.info("Получен запрос на получение всех запросов вещей от пользователя с id=" + userId + " с параметрами " +
                "from=" + from + " size=" + size);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Получен запрос на получение запроса вещи по id=" + requestId + " от пользователя с id=" + userId);
        return itemRequestClient.getRequestById(requestId, userId);
    }
}
