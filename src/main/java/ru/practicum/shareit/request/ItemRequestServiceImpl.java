package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
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
        itemRequestDto.setRequestorId(userId);
        itemRequestDto.setCreated(LocalDateTime.now());

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

        for (ItemRequest request : itemRequestList) {
            responseDtoList.add(ItemRequestMapper.toItemRequestWithItemDtoListResponseDto(request,
                    itemRepository.findAllByRequestId(request.getId()).stream()
                            .map(ItemMapper::toItemDto)
                            .collect(Collectors.toList())));
        }
        return responseDtoList;
    }
}
