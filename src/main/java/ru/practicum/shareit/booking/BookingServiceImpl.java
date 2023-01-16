package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.BookingValidationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public BookingResponseDto saveNewBooking(Long userId, BookingRequestDto bookingRequestDto) {
        User booker = UserMapper.toUser(userService.getById(userId));
        Item item = itemStorage.findById(bookingRequestDto.getItemId())
                .orElseThrow(() ->
                        new NotFoundException("Вещь с id=" + bookingRequestDto.getItemId() + " не существует"));

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нельзя арендовать собственную вещь");
        }

        if (!item.getAvailable()) {
            throw new BookingValidationException("Вещь недоступна для бронирования");
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new BookingValidationException("Время начала бронирования не может быть позже времени окончания");
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDto, booker, item, Status.WAITING);

        return BookingMapper.toBookingResponseDto(bookingStorage.save(booking));
    }

    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingById(bookingId);
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if ((booking.getStatus() == Status.APPROVED && approved) ||
                (booking.getStatus() == Status.REJECTED && !approved)) {
            throw new BookingValidationException("Статус менять не надо");
        }

        if (userId.equals(bookerId)) {
            throw new NotFoundException("Пользователь не может изменять бронирование");
        } else if (!ownerId.equals(userId)) {
            throw new NotFoundException("Пользователь не может изменять бронирование");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        return BookingMapper.toBookingResponseDto(bookingStorage.save(booking));
    }

    @Override
    public BookingResponseDto getById(Long userId, Long bookingId) {
        userService.getById(userId);

        Booking booking = getBookingById(bookingId);
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException("Нет доступа к бронированию");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllUserBookings(Long userId, String bookingState) {
        userService.getById(userId);
        State state = validateState(bookingState);

        switch (state) {
            case CURRENT:
                return bookingStorage.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case PAST:
                return bookingStorage.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case FUTURE:
                return bookingStorage.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case WAITING:
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case REJECTED:
                return bookingStorage.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            default:
                return bookingStorage.findAllByBookerIdOrderByStartDesc(userId).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingResponseDto> getAllUserItemsBookings(Long userId, String bookingState) {
        userService.getById(userId);
        State state = validateState(bookingState);

        switch (state) {
            case CURRENT:
                return bookingStorage.findAllItemOwnerCurrentBookings(userId, LocalDateTime.now(), LocalDateTime.now())
                        .stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case PAST:
                return bookingStorage.findAllItemOwnerPastBookings(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case FUTURE:
                return bookingStorage.findAllItemOwnerFutureBookings(userId, LocalDateTime.now()).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case WAITING:
                return bookingStorage.findAllItemOwnerBookingsByStatus(userId, Status.WAITING).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case REJECTED:
                return bookingStorage.findAllItemOwnerBookingsByStatus(userId, Status.REJECTED).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            default:
                return bookingStorage.findAllItemOwnerBookings(userId).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
        }
    }

    private Booking getBookingById(Long bookingId) {
        return bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id=" + bookingId + " не существует"));
    }

    private State validateState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}
