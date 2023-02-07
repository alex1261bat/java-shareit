package ru.practicum.server.request;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDto saveNewItemRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemDtoListResponseDto> getAllByOwner(Long userId);

    List<ItemRequestWithItemDtoListResponseDto> getAll(Long userId, Pageable pageRequest);

    ItemRequestWithItemDtoListResponseDto getItemRequestById(Long userId, Long requestId);
}
