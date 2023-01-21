package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item toItem(ItemDto itemDto, User owner, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                owner,
                itemRequest
        );
    }

    public static ItemWithBookingDatesDto toItemDtoWithBookingDates(Item item, Booking lastBooking,
                                                                    Booking nextBooking,
                                                                    List<CommentResponseDto> comments) {
        return new ItemWithBookingDatesDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking != null ? new ItemWithBookingDatesDto.BookingShortResponseDto(lastBooking.getId(),
                        lastBooking.getBooker().getId()) : null,
                nextBooking != null ? new ItemWithBookingDatesDto.BookingShortResponseDto(nextBooking.getId(),
                        nextBooking.getBooker().getId()) : null,
                comments
        );
    }
}
