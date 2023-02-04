package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponseDto saveNewBooking(Long userId, BookingRequestDto bookingRequestDto) {
        User booker = userRepository.getUserById(userId);
        Item item = itemRepository.getItemById(bookingRequestDto.getItemId());

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нельзя арендовать собственную вещь");
        }

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Время начала бронирования не может быть позже времени окончания");
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDto, booker, item, Status.WAITING);

        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.getBookingById(bookingId);
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if ((booking.getStatus() == Status.APPROVED && approved) ||
                (booking.getStatus() == Status.REJECTED && !approved)) {
            throw new ValidationException("Статус менять не надо");
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

        return BookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    public BookingResponseDto getById(Long userId, Long bookingId) {
        userRepository.getUserById(userId);

        Booking booking = bookingRepository.getBookingById(bookingId);
        Long ownerId = booking.getItem().getOwner().getId();
        Long bookerId = booking.getBooker().getId();

        if (!userId.equals(bookerId) && !userId.equals(ownerId)) {
            throw new NotFoundException("Нет доступа к бронированию");
        }

        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllUserBookings(Long userId, String bookingState, Pageable pageRequest) {
        userRepository.getUserById(userId);
        State state = validateState(bookingState);

        switch (state) {
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                                LocalDateTime.now(), LocalDateTime.now(), pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, LocalDateTime.now(),
                                pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, LocalDateTime.now(),
                                pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING,
                                pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED,
                                pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            default:
                return bookingRepository.findAllByBookerIdOrderByStartDesc(userId, pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingResponseDto> getAllUserItemsBookings(Long userId, String bookingState, Pageable pageRequest) {
        userRepository.getUserById(userId);
        State state = validateState(bookingState);

        switch (state) {
            case CURRENT:
                return bookingRepository.findAllItemOwnerCurrentBookings(userId, LocalDateTime.now(),
                                LocalDateTime.now(), pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllItemOwnerPastBookings(userId, LocalDateTime.now(),
                                pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllItemOwnerFutureBookings(userId, LocalDateTime.now(),
                                pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllItemOwnerBookingsByStatus(userId, Status.WAITING, pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllItemOwnerBookingsByStatus(userId, Status.REJECTED,
                                pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
            default:
                return bookingRepository.findAllItemOwnerBookings(userId, pageRequest).stream()
                        .map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
        }
    }

    private State validateState(String state) {
        try {
            return State.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Unknown state: " + state);
        }
    }
}
