package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.exceptions.ValidationException;
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
    private BookingService bookingService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private BookingRepository bookingRepository;
    private final User user = new User(1L, "name1", "email1@mail");
    private final User user2 = new User(2L, "name2", "email2@mail");
    private final Item item =
            new Item(1L, "itemName", "itemDescription", true, user, null);
    private final BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2), item.getId());
    private final Booking booking1 = new Booking(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2), item, user2, Status.WAITING);

    @BeforeEach
    void init() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemRepository, userRepository);
    }

    @Test
    void saveNewBookingTest() {
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(booking, user2, item, Status.WAITING));
        when(itemRepository.save(any())).thenReturn(item);
        when(userRepository.getUserById(any())).thenReturn(user2);
        when(itemRepository.getItemById(any())).thenReturn(item);

        BookingResponseDto bookingDto = bookingService.saveNewBooking(user2.getId(), booking);

        assertEquals(booking.getId(), bookingDto.getId());
    }

    @Test
    void approveBookingTest() {
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(booking, user2, item, Status.APPROVED));
        when(bookingRepository.getBookingById(any())).thenReturn((BookingMapper.toBooking(booking, user2, item,
                Status.WAITING)));
        when(itemRepository.getItemById(any())).thenReturn(item);

        BookingResponseDto bookingDto = bookingService.approveBooking(user.getId(), booking.getId(), true);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getItemId(), bookingDto.getItem().getId());
        assertEquals(Status.APPROVED, bookingDto.getStatus());
    }

    @Test
    void getByIdTest() {
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
    void getAllUserBookingsWhenStateCurrentTest() {
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(),
                any())).thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserBookings(user2.getId(), "CURRENT",
                Pageable.unpaged());

        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserBookingsWhenStatePastTest() {
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserBookings(user2.getId(), "PAST",
                Pageable.unpaged());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserBookingsWhenStateFutureTest() {
        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserBookings(user2.getId(), "FUTURE",
                Pageable.unpaged());

        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserBookingsWhenStateWaitingTest() {
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserBookings(user2.getId(), "WAITING",
                Pageable.unpaged());

        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserBookingsWhenStateRejectedTest() {
        when(bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserBookings(user2.getId(), "REJECTED",
                Pageable.unpaged());

        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserBookingsWhenStateAllTest() {
        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserBookings(user2.getId(), "ALL",
                Pageable.unpaged());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserItemsBookingsWhenStateCurrentTest() {
        when(bookingRepository.findAllItemOwnerCurrentBookings(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserItemsBookings(user2.getId(), "CURRENT",
                Pageable.unpaged());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserItemsBookingsWhenStatePastTest() {
        when(bookingRepository.findAllItemOwnerPastBookings(anyLong(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserItemsBookings(user2.getId(), "PAST",
                Pageable.unpaged());

        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserItemsBookingsWhenStateFutureTest() {
        when(bookingRepository.findAllItemOwnerFutureBookings(anyLong(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserItemsBookings(user2.getId(), "FUTURE",
                Pageable.unpaged());

        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserItemsBookingsWhenStateWaitingTest() {
        when(bookingRepository.findAllItemOwnerBookingsByStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserItemsBookings(user2.getId(), "WAITING",
                Pageable.unpaged());

        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserItemsBookingsWhenStateRejectedTest() {
        when(bookingRepository.findAllItemOwnerBookingsByStatus(anyLong(), any(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserItemsBookings(user2.getId(), "REJECTED",
                Pageable.unpaged());

        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void getAllUserItemsBookingsWhenStateAllTest() {
        when(bookingRepository.findAllItemOwnerBookings(anyLong(), any()))
                .thenReturn(List.of(booking1));

        List<BookingResponseDto> bookingList = bookingService.getAllUserItemsBookings(user2.getId(), "ALL",
                Pageable.unpaged());

        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem().getId(), bookingList.get(0).getItem().getId());
        assertEquals(booking1.getBooker().getId(), bookingList.get(0).getBooker().getId());
    }

    @Test
    void createWrongUserTest() {
        when(userRepository.getUserById(any()))
                .thenThrow(new NotFoundException("Пользователь с id=" + -1L + " не существует"));
        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.saveNewBooking(-1L, booking));
        assertEquals("Пользователь с id=" + -1L + " не существует", e.getMessage());
    }

    @Test
    void createWrongItemTest() {
        when(userRepository.getUserById(any())).thenReturn(user);
        when(itemRepository.getItemById(any()))
                .thenThrow(new NotFoundException("Вещь с id=" + 2L + " не существует"));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.saveNewBooking(2L, booking));
        assertEquals("Вещь с id=" + 2L + " не существует", e.getMessage());
    }

    @Test
    void createWrongBookerTest() {
        when(userRepository.getUserById(anyLong())).thenReturn(user);
        when(itemRepository.getItemById(anyLong())).thenReturn(item);

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.saveNewBooking(1L, booking));
        assertEquals("Нельзя арендовать собственную вещь", e.getMessage());
    }

    @Test
    void createNotAvailableTest() {
        item.setAvailable(false);

        when(userRepository.getUserById(any())).thenReturn(user2);
        when(itemRepository.getItemById(any())).thenReturn(item);

        ValidationException e = assertThrows(ValidationException.class, () ->
                bookingService.saveNewBooking(2L, booking));
        assertEquals("Вещь недоступна для бронирования", e.getMessage());
    }

    @Test
    void createWrongTimeTest() {
        BookingRequestDto booking = new BookingRequestDto(1L, LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(1), item.getId());

        when(userRepository.getUserById(any())).thenReturn(user2);
        when(itemRepository.getItemById(any())).thenReturn(item);

        ValidationException e = assertThrows(ValidationException.class, () ->
                bookingService.saveNewBooking(2L, booking));
        assertEquals("Время начала бронирования не может быть позже времени окончания", e.getMessage());
    }

    @Test
    void approveBookingWrongBookingTest() {
        when(bookingRepository.getBookingById(any()))
                .thenThrow(new NotFoundException("Бронирование с id=" + booking.getId() + " не существует"));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.approveBooking(2L, booking.getId(), true));
        assertEquals("Бронирование с id=" + booking.getId() + " не существует", e.getMessage());
    }

    @Test
    void approveBookingWrongStatusTest() {
        when(bookingRepository.getBookingById(any())).thenReturn(BookingMapper.toBooking(booking, user2, item,
                Status.APPROVED));

        ValidationException e = assertThrows(ValidationException.class, () ->
                bookingService.approveBooking(2L, booking.getId(), true));
        assertEquals("Статус менять не надо", e.getMessage());
    }

    @Test
    void approveBookingWrongBookerTest() {
        when(bookingRepository.getBookingById(any())).thenReturn(BookingMapper.toBooking(booking, user2, item,
                Status.WAITING));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.approveBooking(2L, booking.getId(), true));
        assertEquals("Пользователь не может изменять бронирование", e.getMessage());
    }

    @Test
    void getBookingByIdWrongUserTest() {
        when(userRepository.getUserById(anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id=" + 2L + " не существует"));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.getById(2L, booking.getId()));
        assertEquals("Пользователь с id=" + 2L + " не существует", e.getMessage());
    }

    @Test
    void getBookingByIdWrongBookerTest() {
        when(userRepository.getUserById(anyLong())).thenReturn(user);
        when(bookingRepository.getBookingById(anyLong())).thenReturn(BookingMapper.toBooking(booking, user, item,
                Status.WAITING));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.getById(2L, booking.getId()));
        assertEquals("Нет доступа к бронированию", e.getMessage());
    }

    @Test
    void getBookingListWrongUserTest() {
        when(userRepository.getUserById(anyLong()))
                .thenThrow(new NotFoundException("Пользователь с id=" + user.getId() + " не существует"));

        NotFoundException e = assertThrows(NotFoundException.class, () ->
                bookingService.getAllUserBookings(user.getId(), "ALL", Pageable.unpaged()));
        assertEquals("Пользователь с id=" + user.getId() + " не существует", e.getMessage());
    }

    @Test
    void getBookingListWrongStateTest() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                bookingService.getAllUserBookings(user.getId(), "WRONGSTATE", Pageable.unpaged()));
        assertEquals("Unknown state: WRONGSTATE", e.getMessage());
    }

    @Test
    void getOwnerBookingListWrongStateTest() {
        ValidationException e = assertThrows(ValidationException.class, () ->
                bookingService.getAllUserItemsBookings(user.getId(), "WRONGSTATE", Pageable.unpaged()));
        assertEquals("Unknown state: WRONGSTATE", e.getMessage());
    }
}
