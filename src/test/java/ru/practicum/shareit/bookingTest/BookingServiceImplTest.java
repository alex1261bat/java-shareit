package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {
    BookingService bookingService;
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;

    @BeforeEach
    void init() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void saveNewBookingTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(booking, user2, item, Status.WAITING));
        when(itemRepository.save(any())).thenReturn(item);
        when(userRepository.getUserById(any())).thenReturn(user2);
        when(itemRepository.getItemById(any())).thenReturn(item);

        BookingResponseDto bookingDto = bookingService.saveNewBooking(user2.getId(), booking);

        assertEquals(booking.getId(), bookingDto.getId());
    }

    @Test
    void approveBookingTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());


        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(booking, user2, item, Status.APPROVED));
        when(bookingRepository.getBookingById(any())).thenReturn((BookingMapper.toBooking(booking, user2, item,
                Status.WAITING)));
        when(itemRepository.getItemById(any())).thenReturn(item);

        bookingService.saveNewBooking(user2.getId(), booking);
        BookingResponseDto bookingDto = bookingService.approveBooking(user.getId(), booking.getId(), true);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getItemId(), bookingDto.getItem().getId());
        assertEquals(Status.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getByIdTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());
        when(bookingRepository.getBookingById(any())).thenReturn(BookingMapper.toBooking(booking, user2, item,
                Status.WAITING));
        when(userRepository.getUserById(anyLong())).thenReturn(user2);
        BookingResponseDto bookingDto = bookingService.getById(user2.getId(), booking.getId());

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getItemId(), bookingDto.getItem().getId());
    }

    @Test
    void getAllUserBookingsTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, user2, Status.WAITING);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(),
                any())).thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> bookingList = bookingService.getAllUserBookings(user2.getId(), "CURRENT",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList.get(0).getId());

        List<BookingResponseDto> bookingList1 = bookingService.getAllUserBookings(user2.getId(), "PAST",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList1.get(0).getId());
        assertEquals(booking.getStart(), bookingList1.get(0).getStart());
        assertEquals(booking.getEnd(), bookingList1.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookingList1.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingList1.get(0).getBooker().getId());
        List<BookingResponseDto> bookingList2 = bookingService.getAllUserBookings(user2.getId(), "FUTURE",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList2.get(0).getId());
        assertEquals(booking.getStart(), bookingList2.get(0).getStart());
        assertEquals(booking.getEnd(), bookingList2.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookingList2.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingList2.get(0).getBooker().getId());
        List<BookingResponseDto> bookingList3 = bookingService.getAllUserBookings(user2.getId(), "WAITING",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList3.get(0).getId());
        assertEquals(booking.getStart(), bookingList3.get(0).getStart());
        assertEquals(booking.getEnd(), bookingList3.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookingList3.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingList3.get(0).getBooker().getId());
        List<BookingResponseDto> bookingList4 = bookingService.getAllUserBookings(user2.getId(), "REJECTED",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList4.get(0).getId());
        assertEquals(booking.getStart(), bookingList4.get(0).getStart());
        assertEquals(booking.getEnd(), bookingList4.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookingList4.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingList4.get(0).getBooker().getId());
        List<BookingResponseDto> bookingList5 = bookingService.getAllUserBookings(user2.getId(), "ALL",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList5.get(0).getId());
        assertEquals(booking.getStart(), bookingList5.get(0).getStart());
        assertEquals(booking.getEnd(), bookingList5.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookingList5.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingList5.get(0).getBooker().getId());
    }

    @Test
    void getAllUserItemsBookingsTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item, user2, Status.WAITING);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        when(bookingRepository.findAllItemOwnerCurrentBookings(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllItemOwnerPastBookings(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllItemOwnerFutureBookings(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllItemOwnerBookingsByStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllItemOwnerBookingsByStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findAllItemOwnerBookings(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> bookingList = bookingService.getAllUserItemsBookings(user2.getId(), "CURRENT",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList.get(0).getId());

        List<BookingResponseDto> bookingList1 = bookingService.getAllUserItemsBookings(user2.getId(), "PAST",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList1.get(0).getId());
        assertEquals(booking.getStart(), bookingList1.get(0).getStart());
        assertEquals(booking.getEnd(), bookingList1.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookingList1.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingList1.get(0).getBooker().getId());
        List<BookingResponseDto> bookingList2 = bookingService.getAllUserItemsBookings(user2.getId(), "FUTURE",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList2.get(0).getId());
        assertEquals(booking.getStart(), bookingList2.get(0).getStart());
        assertEquals(booking.getEnd(), bookingList2.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookingList2.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingList2.get(0).getBooker().getId());
        List<BookingResponseDto> bookingList3 = bookingService.getAllUserItemsBookings(user2.getId(), "WAITING",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList3.get(0).getId());
        assertEquals(booking.getStart(), bookingList3.get(0).getStart());
        assertEquals(booking.getEnd(), bookingList3.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookingList3.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingList3.get(0).getBooker().getId());
        List<BookingResponseDto> bookingList4 = bookingService.getAllUserItemsBookings(user2.getId(), "REJECTED",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList4.get(0).getId());
        assertEquals(booking.getStart(), bookingList4.get(0).getStart());
        assertEquals(booking.getEnd(), bookingList4.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookingList4.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingList4.get(0).getBooker().getId());
        List<BookingResponseDto> bookingList5 = bookingService.getAllUserItemsBookings(user2.getId(), "ALL",
                Pageable.unpaged());
        assertEquals(booking.getId(), bookingList5.get(0).getId());
        assertEquals(booking.getStart(), bookingList5.get(0).getStart());
        assertEquals(booking.getEnd(), bookingList5.get(0).getEnd());
        assertEquals(booking.getItem().getId(), bookingList5.get(0).getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingList5.get(0).getBooker().getId());
    }

    @Test
    void createWrongUserTest() {
        User user = new User(1L, "name1", "email1@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

        when(userRepository.getUserById(any()))
                .thenThrow(new NotFoundException("Пользователь с id=" + -1L + " не существует"));
        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.saveNewBooking(-1L, booking));
        assertEquals("Пользователь с id=" + -1L + " не существует", e.getMessage());
    }

    @Test
    void createWrongItemTest() {
        User user = new User(1L, "name1", "email1@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

        when(userRepository.getUserById(any())).thenReturn(user);
        when(itemRepository.getItemById(any()))
                .thenThrow(new NotFoundException("Вещь с id=" + 2L + " не существует"));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.saveNewBooking(2L, booking));
        assertEquals("Вещь с id=" + 2L + " не существует", e.getMessage());
    }

    @Test
    void createWrongBookerTest() {
        User user = new User(1L, "name1", "email1@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

        when(userRepository.getUserById(anyLong())).thenReturn(user);
        when(itemRepository.getItemById(anyLong())).thenReturn(item);

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.saveNewBooking(1L, booking));
        assertEquals("Нельзя арендовать собственную вещь", e.getMessage());
    }

    @Test
    void createNotAvailableTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "itemDescription", false, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

        when(userRepository.getUserById(any())).thenReturn(user2);
        when(itemRepository.getItemById(any())).thenReturn(item);

        BookingValidationException e = assertThrows(BookingValidationException.class, () ->
                bookingService.saveNewBooking(2L, booking));
        assertEquals("Вещь недоступна для бронирования", e.getMessage());
    }

    @Test
    void createWrongTimeTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1), item.getId());

        when(userRepository.getUserById(any())).thenReturn(user2);
        when(itemRepository.getItemById(any())).thenReturn(item);

        BookingValidationException e = assertThrows(BookingValidationException.class, () ->
                bookingService.saveNewBooking(2L, booking));
        assertEquals("Время начала бронирования не может быть позже времени окончания", e.getMessage());
    }

    @Test
    void approveBookingWrongBookingTest() {
        User user = new User(1L, "name1", "email1@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

        when(bookingRepository.getBookingById(any()))
                .thenThrow(new NotFoundException("Бронирование с id=" + booking.getId() + " не существует"));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.approveBooking(2L, booking.getId(), true));
        assertEquals("Бронирование с id=" + booking.getId() + " не существует", e.getMessage());
    }

    @Test
    void approveBookingWrongStatusTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

        when(bookingRepository.getBookingById(any())).thenReturn(BookingMapper.toBooking(booking, user2, item,
                Status.APPROVED));

        BookingValidationException e = assertThrows(BookingValidationException.class, () ->
                bookingService.approveBooking(2L, booking.getId(), true));
        assertEquals("Статус менять не надо", e.getMessage());
    }

    @Test
    void approveBookingWrongBookerTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user2 = new User(2L, "name2", "email2@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

        when(bookingRepository.getBookingById(any())).thenReturn(BookingMapper.toBooking(booking, user2, item,
                Status.WAITING));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.approveBooking(2L, booking.getId(), true));
        assertEquals("Пользователь не может изменять бронирование", e.getMessage());
    }

    @Test
    void getBookingByIdWrongUserTest() {
        User user = new User(1L, "name1", "email1@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

        when(userRepository.getUserById(anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id=" + 2L + " не существует"));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.getById(2L, booking.getId()));
        assertEquals("Пользователь с id=" + 2L + " не существует", e.getMessage());
    }

    @Test
    void getBookingByIdWrongBookerTest() {
        User user = new User(1L, "name1", "email1@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user, null);

        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), item.getId());

        when(userRepository.getUserById(anyLong())).thenReturn(user);
        when(bookingRepository.getBookingById(anyLong())).thenReturn(BookingMapper.toBooking(booking, user, item,
                Status.WAITING));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.getById(2L, booking.getId()));
        assertEquals("Нет доступа к бронированию", e.getMessage());
    }

    @Test
    void getBookingListWrongUserTest() {
        User user = new User(1L, "name1", "email1@mail");

        when(userRepository.getUserById(anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id=" + user.getId() + " не существует"));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.getAllUserBookings(user.getId(), "ALL", Pageable.unpaged()));
        assertEquals("Пользователь с id=" + user.getId() + " не существует", e.getMessage());
    }

    @Test
    void getBookingListWrongStateTest() {
        User user = new User(1L, "name1", "email1@mail");

        BookingValidationException e = assertThrows(BookingValidationException.class, () ->
                bookingService.getAllUserBookings(user.getId(), "WRONGSTATE", Pageable.unpaged()));
        assertEquals("Unknown state: WRONGSTATE", e.getMessage());
    }

    @Test
    void getOwnerBookingListWrongStateTest() {
        User user = new User(1L, "name1", "email1@mail");

        BookingValidationException e = assertThrows(BookingValidationException.class, () ->
                bookingService.getAllUserItemsBookings(user.getId(), "WRONGSTATE", Pageable.unpaged()));
        assertEquals("Unknown state: WRONGSTATE", e.getMessage());
    }
}
