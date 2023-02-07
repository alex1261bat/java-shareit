package ru.practicum.server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemDto;
import ru.practicum.server.item.ItemMapper;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemRequestDto saveNewItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requestor = userRepository.getUserById(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);

        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestWithItemDtoListResponseDto> getAllByOwner(Long userId) {
        userRepository.getUserById(userId);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(userId);

        return convertItemRequestListToDtoList(itemRequestList);
    }

    @Override
    public List<ItemRequestWithItemDtoListResponseDto> getAll(Long userId, Pageable pageRequest) {
        userRepository.getUserById(userId);
        List<ItemRequest> itemRequestList = itemRequestRepository.findAllByOtherUser(userId, pageRequest);

        return convertItemRequestListToDtoList(itemRequestList);
    }

    @Override
    public ItemRequestWithItemDtoListResponseDto getItemRequestById(Long userId, Long itemRequestId) {
        userRepository.getUserById(userId);
        ItemRequest itemRequest = itemRequestRepository.getItemRequestById(itemRequestId);

        return ItemRequestMapper.toItemRequestWithItemDtoListResponseDto(itemRequest,
                itemRepository.findAllByRequestId(itemRequest.getId()).stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList()));
    }

    private List<ItemRequestWithItemDtoListResponseDto> convertItemRequestListToDtoList(
            List<ItemRequest> itemRequestList) {
        List<ItemRequestWithItemDtoListResponseDto> responseDtoList = new ArrayList<>();
        List<Item> items = itemRepository.findAllByRequestIsIn(itemRequestList);
        List<ItemDto> itemDtoList = convertItemListToItemDtoList(items);

        for (ItemRequest request : itemRequestList) {
            responseDtoList.add(ItemRequestMapper.toItemRequestWithItemDtoListResponseDto(request,
                    itemDtoList.stream()
                            .filter(itemDto -> itemDto.getRequestId().equals(request.getId()))
                            .collect(Collectors.toList())));
        }
        return responseDtoList;
    }

    private List<ItemDto> convertItemListToItemDtoList(List<Item> items) {
        List<ItemDto> itemDtoList = new ArrayList<>();

        for (Item item : items) {
            itemDtoList.add(ItemMapper.toItemDto(item));
        }
        return itemDtoList;
    }
}
