package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponseDto saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody BookingRequestDto bookingDto) {
        return bookingService.saveNewBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingService.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(name = "state",
                                                                     defaultValue = "ALL") String state) {
        return bookingService.getAllUserBookings(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllUserItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(name = "state",
                                                                          defaultValue = "ALL") String state) {
        return bookingService.getAllUserItemsBookings(userId, state);
    }
}
