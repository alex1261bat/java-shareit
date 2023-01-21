package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.*;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceImplTest {
    ItemService itemService;
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;
    ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void init() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository,
                itemRequestRepository);
    }

    @Test
    void saveNewItemTest() {
        User user = new User(1L, "name1", "email1@mail");
        ItemDto itemDto = new ItemDto(1L, "itemName", "description", true, null,
                null);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDto, user, null));

        ItemDto itemRequest = itemService.saveNewItem(user.getId(), itemDto);

        assertEquals(itemDto.getId(), itemRequest.getId());
        assertEquals(itemDto.getName(), itemRequest.getName());
        assertEquals(itemDto.getAvailable(), itemRequest.getAvailable());
        assertEquals(itemDto.getDescription(), itemRequest.getDescription());
    }

    @Test
    void updateTest() {
        User user = new User(1L, "name1", "email1@mail");
        ItemDto itemDto = new ItemDto(1L, "UpdatedItemName","description", null,
                null, null);
        Item updatedItem = new Item(1L, "itemName", "description", true,
                user, null);

        when(userRepository.getUserById(any())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDto, user, null));
        when(itemRepository.getItemById(any())).thenReturn(updatedItem);

        ItemDto itemRequest = itemService.update(user.getId(), itemDto.getId(), itemDto);

        assertEquals(updatedItem.getName(), itemRequest.getName());
        assertEquals(updatedItem.getId(), itemRequest.getId());
        assertEquals(updatedItem.getAvailable(), itemRequest.getAvailable());
        assertEquals(updatedItem.getDescription(), itemRequest.getDescription());
    }

    @Test
    void getByIdTest() {
        User user = new User(1L, "name1", "email1@mail");
        Item item = new Item(1L, "itemName", "desc", true, user, null);

        when(itemRepository.getItemById(anyLong())).thenReturn(item);
        when(bookingRepository.findAllByItemId(any())).thenReturn(new ArrayList<>());
        when(commentRepository.findAllByItemId(any())).thenReturn(new ArrayList<>());

        ItemWithBookingDatesDto itemDto = itemService.getById(user.getId(), item.getId());

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getDescription(), itemDto.getDescription());
    }

    @Test
    void getUserItemsTest() {
        User user = new User(1L, "name1", "email1@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(Collections.singletonList(item));
        when(bookingRepository.findAllByItemId(any())).thenReturn(new ArrayList<>());
        when(commentRepository.findAllByItemId(any())).thenReturn(new ArrayList<>());

        List<ItemWithBookingDatesDto> itemList = itemService.getUserItems(user.getId(), Pageable.ofSize(10));

        assertEquals(1, itemList.size());
        assertEquals(item.getId(), itemList.get(0).getId());
        assertEquals(item.getName(), itemList.get(0).getName());
        assertEquals(item.getAvailable(), itemList.get(0).getAvailable());
        assertEquals(item.getDescription(), itemList.get(0).getDescription());
    }

    @Test
    void findAvailableItems() {
        User user = new User(1L, "name1", "email1@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        when(itemRepository.findAvailableItems(anyString(), any())).thenReturn(Collections.singletonList(item));
        List<ItemDto> itemList = itemService.findAvailableItems("itemName", Pageable.ofSize(10));

        assertEquals(1, itemList.size());
        assertEquals(item.getId(), itemList.get(0).getId());
        assertEquals(item.getName(), itemList.get(0).getName());
        assertEquals(item.getAvailable(), itemList.get(0).getAvailable());
        assertEquals(item.getDescription(), itemList.get(0).getDescription());
    }

    @Test
    void lastNextBookingTest() {
        User user1 = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        User user3 = new User(3L, "name3", "email3@mail");
        User user4 = new User(4L, "name4", "email4@mail");

        Item item = new Item(1L, "itemName", "itemDescription", true, user1, null);

        Booking booking1 = new Booking(1L, LocalDateTime.of(2022, 1, 1, 1, 1),
                LocalDateTime.of(2022, 1, 2, 1, 1), item, user2, Status.APPROVED);
        Booking booking2 = new Booking(2L, LocalDateTime.of(2022, 2, 1, 1, 1),
                LocalDateTime.of(2022, 2, 2, 1, 1), item, user3, Status.APPROVED);
        Booking booking3 = new Booking(3L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, user4, Status.APPROVED);

        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(Collections.singletonList(item));
        when(bookingRepository.findAllByItemId(any())).thenReturn(List.of(booking1, booking2, booking3));
        when(commentRepository.findAllByItemId(any())).thenReturn(new ArrayList<>());

        List<ItemWithBookingDatesDto> itemList = itemService.getUserItems(user1.getId(), Pageable.ofSize(10));

        assertEquals(1, itemList.size());
        assertEquals(item.getId(), itemList.get(0).getId());
        assertEquals(item.getName(), itemList.get(0).getName());
        assertEquals(item.getAvailable(), itemList.get(0).getAvailable());
        assertEquals(item.getDescription(), itemList.get(0).getDescription());
        assertEquals(booking3.getId(), itemList.get(0).getNextBooking().getId());
        assertEquals(booking2.getId(), itemList.get(0).getLastBooking().getId());
    }

    @Test
    void addCommentTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "description", true, user, null);
        Booking booking1 = new Booking(1L, LocalDateTime.of(2022, 1, 1, 1, 1),
                LocalDateTime.of(2022, 1, 2, 1, 1), item, user2, Status.APPROVED);
        CommentRequestDto commentDto = new CommentRequestDto(1L, "text", item.getId(), user2.getId(),
                LocalDateTime.now());

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllUserBookings(anyLong(), anyLong(), any())).thenReturn(List.of(booking1));
        when(commentRepository.save(any())).thenReturn(CommentMapper.toComment(commentDto, item, user2));

        CommentResponseDto newComment = itemService.addComment(user2.getId(), item.getId(), commentDto);

        assertEquals(newComment.getId(), commentDto.getId());
        assertEquals(newComment.getText(), commentDto.getText());
    }

    @Test
    void updateWrongUserTest() {
        ItemDto itemDto = new ItemDto(1L, "UpdatedItemName", "desc", null, null,
                null);
        when(userRepository.getUserById(any()))
                .thenThrow(new NotFoundException("Пользователь с id=" + itemDto.getOwner() + " не существует"));
        NotFoundException e = assertThrows(NotFoundException.class, () ->
                itemService.update(2L, 1L, itemDto));
        assertEquals("Пользователь с id=" + itemDto.getOwner() + " не существует", e.getMessage());
    }

    @Test
    void updateWrongItemTest() {
        User user = new User(1L, "name1", "email1@mail");
        ItemDto itemDto = new ItemDto(1L, "UpdatedItemName", "desc", null, null,
                null);

        when(userRepository.getUserById(any())).thenReturn(user);
        when(itemRepository.getItemById(any()))
                .thenThrow(new NotFoundException("Вещь с id=" + itemDto.getId() + " не существует"));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                itemService.update(1L, 1L, itemDto));
        assertEquals("Вещь с id=" + itemDto.getId() + " не существует", e.getMessage());
    }

    @Test
    void updateWrongOwnerTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        ItemDto itemDto = new ItemDto(1L, "UpdatedItemName", "desc", null, null,
                null);

        when(userRepository.getUserById(anyLong())).thenReturn(user2);
        when(itemRepository.getItemById(anyLong()))
                .thenReturn(ItemMapper.toItem(itemDto, user, null));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                itemService.update(2L, 1L, itemDto));
        assertEquals("Вещь с id=" + 1L + " не принадлежит пользователю с id="
                + user2.getId(), e.getMessage());
    }

    @Test
    void findItemByIdWrongIdTest() {
        when(itemRepository.getItemById(any())).thenThrow(new NotFoundException("Вещь с id=" + 1L + " не существует"));
        NotFoundException e = assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
        assertEquals("Вещь с id=" + 1L + " не существует", e.getMessage());
    }

    @Test
    void addCommentWrongUserTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "description", true, user, null);
        CommentRequestDto commentDto = new CommentRequestDto(1L, "text", item.getId(), user2.getId(),
                LocalDateTime.now());

        when(userRepository.findById(any())).thenReturn(Optional.empty());
        BookingValidationException e = assertThrows(BookingValidationException.class, () ->
                itemService.addComment(3L, 2L, commentDto));
        assertEquals("Пользователь с id=3 не брал вещи в аренду", e.getMessage());
    }

    @Test
    void addCommentWrongItemTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "description", true, user, null);
        CommentRequestDto commentDto = new CommentRequestDto(1L, "text", item.getId(), user2.getId(),
                LocalDateTime.now());

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.empty());
        BookingValidationException e = assertThrows(BookingValidationException.class, () ->
                itemService.addComment(user.getId(), 3L, commentDto));
        assertEquals("Пользователь с id=" + user.getId() + " не брал вещи в аренду", e.getMessage());
    }

    @Test
    void addCommentWrongBookingTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "description", true, user, null);
        CommentRequestDto commentDto = new CommentRequestDto(1L, "text", item.getId(), user2.getId(),
                LocalDateTime.now());

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllUserBookings(anyLong(), anyLong(), any())).thenReturn(new ArrayList<>());

        BookingValidationException e = assertThrows(BookingValidationException.class, () ->
                itemService.addComment(3L, 1L, commentDto));
        assertEquals("Пользователь с id=" + 3L + " не брал вещи в аренду", e.getMessage());
    }
}
