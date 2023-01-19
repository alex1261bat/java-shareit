package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingResponseDto saveNewBooking(Long userId, BookingRequestDto bookingDto);

    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean approved);

    BookingResponseDto getById(Long userId, Long bookingId);

    List<BookingResponseDto> getAllUserBookings(Long userId, String state);

    List<BookingResponseDto> getAllUserItemsBookings(Long userId, String state);
}
