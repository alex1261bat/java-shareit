package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.pageCreator.PageCreator;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto saveNewItemRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.saveNewItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestWithItemDtoListResponseDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllByOwner(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemDtoListResponseDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                              @RequestParam(name = "from",
                                                                            defaultValue = "0") Integer from,
                                                              @RequestParam(name = "size",
                                                                            defaultValue = "10") Integer size) {
        return itemRequestService.getAll(userId, PageCreator.createPage(from, size));
    }

    @GetMapping("{requestId}")
    public ItemRequestWithItemDtoListResponseDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                                @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(userId, requestId);
    }
}
