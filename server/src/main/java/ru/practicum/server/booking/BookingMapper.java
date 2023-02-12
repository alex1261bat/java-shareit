package ru.practicum.server.booking;

import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemMapper;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserMapper;

public class BookingMapper {
    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                booking.getStatus(),
                UserMapper.toUserDto(booking.getBooker())
        );
    }

    public static Booking toBooking(BookingRequestDto bookingRequestDto, User booker, Item item, Status status) {
        return new Booking(
                bookingRequestDto.getId(),
                bookingRequestDto.getStart(),
                bookingRequestDto.getEnd(),
                item,
                booker,
                status
        );
    }
}
