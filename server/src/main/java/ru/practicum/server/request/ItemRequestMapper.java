package ru.practicum.server.request;

import ru.practicum.server.item.ItemDto;
import ru.practicum.server.user.User;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User requestor) {
        return new ItemRequest(
                itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requestor,
                itemRequestDto.getCreated()
        );
    }

    public static ItemRequestWithItemDtoListResponseDto toItemRequestWithItemDtoListResponseDto(
            ItemRequest itemRequest, List<ItemDto> items) {
        return new ItemRequestWithItemDtoListResponseDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                itemRequest.getRequestor().getId() != null ? itemRequest.getRequestor().getId() : null
        );
    }
}
