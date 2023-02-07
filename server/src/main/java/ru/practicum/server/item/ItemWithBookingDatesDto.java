package ru.practicum.server.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingDatesDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private BookingShortResponseDto lastBooking;
    private BookingShortResponseDto nextBooking;
    private List<CommentResponseDto> comments;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingShortResponseDto {
        private Long id;
        private Long bookerId;
    }
}
