package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.*;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceImplTest {
    ItemRepository itemRepository;
    UserRepository userRepository;
    ItemRequestRepository itemRequestRepository;
    ItemRequestServiceImpl itemRequestService;

    @BeforeEach
    void init() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, itemRepository, userRepository);
    }

    @Test
    void saveNewItemRequestTest() {
        User user = new User(1L, "name1", "email1@mail");
        ItemRequestDto request = new ItemRequestDto(1L, "text",  LocalDateTime.now(), user.getId());

        when(itemRequestRepository.save(any())).thenReturn(ItemRequestMapper.toItemRequest(request, user));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ItemRequestDto requestDto = itemRequestService.saveNewItemRequest(user.getId(), request);

        assertEquals(request.getId(), requestDto.getId());
        assertEquals(request.getDescription(), requestDto.getDescription());
        assertEquals(request.getRequestorId(), requestDto.getRequestorId());
    }

    @Test
    void getAllByOwnerTest() {
        User user = new User(1L, "name1", "email1@mail");
        User owner = new User(1L, "owner", "owner@mail");

        ItemRequestDto request = new ItemRequestDto(1L, "text", LocalDateTime.now(),user.getId());
        Item item = new Item(1L, "item", "description", true, owner, ItemRequestMapper
                .toItemRequest(request, user));
        when(itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(ItemRequestMapper.toItemRequest(request, user)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        List<ItemRequestWithItemDtoListResponseDto> requestList = itemRequestService.getAllByOwner(user.getId());

        assertEquals(request.getId(), requestList.get(0).getId());
        assertEquals(request.getDescription(), requestList.get(0).getDescription());
        assertEquals(item.getId(), requestList.get(0).getItems().get(0).getId());
        assertEquals(item.getName(), requestList.get(0).getItems().get(0).getName());
    }

    @Test
    void getAllTest() {
        User user = new User(1L, "name1", "email1@mail");
        User owner = new User(1L, "owner", "owner@mail");

        ItemRequestDto request = new ItemRequestDto(1L, "text", LocalDateTime.now(), user.getId());
        Item item = new Item(1L, "item", "description", true, owner, ItemRequestMapper
                .toItemRequest(request, user));
        when(itemRequestRepository.findAllByOtherUser(anyLong(), any())).thenReturn(List.of(ItemRequestMapper
                .toItemRequest(request, user)));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        List<ItemRequestWithItemDtoListResponseDto> requestList = itemRequestService
                .getAll(owner.getId(), Pageable.unpaged());

        assertEquals(request.getId(), requestList.get(0).getId());
        assertEquals(request.getDescription(), requestList.get(0).getDescription());
        assertEquals(item.getId(), requestList.get(0).getItems().get(0).getId());
        assertEquals(item.getName(), requestList.get(0).getItems().get(0).getName());
    }

    @Test
    void getItemRequestByIdTest() {
        User user = new User(1L, "name1", "email1@mail");
        User owner = new User(1L, "owner", "owner@mail");

        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "text",  LocalDateTime.now(),user.getId());
        Item item = new Item(1L, "item", "description", true, owner,
                ItemRequestMapper.toItemRequest(itemRequestDto, user));
        when(itemRequestRepository.getItemRequestById(anyLong()))
                .thenReturn(ItemRequestMapper.toItemRequest(itemRequestDto, user));
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findAllByRequestId(anyLong())).thenReturn(List.of(item));

        ItemRequestWithItemDtoListResponseDto requestDto = itemRequestService
                .getItemRequestById(owner.getId(), itemRequestDto.getId());

        assertEquals(itemRequestDto.getId(), requestDto.getId());
        assertEquals(itemRequestDto.getDescription(), requestDto.getDescription());
        assertEquals(item.getId(), requestDto.getItems().get(0).getId());
        assertEquals(item.getName(), requestDto.getItems().get(0).getName());
    }
}
