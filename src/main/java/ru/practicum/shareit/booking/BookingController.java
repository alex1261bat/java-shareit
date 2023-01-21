package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.ValidationException;
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
    public BookingResponseDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestParam(name = "state",
                                                                     defaultValue = "ALL") String state,
                                                       @RequestParam(name = "from",
                                                                     defaultValue = "0") Integer from,
                                                       @RequestParam(name = "size",
                                                                     defaultValue = "10") Integer size) {
        return bookingService.getAllUserBookings(userId, state, validatePage(from, size));
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllUserItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                            @RequestParam(name = "state",
                                                                          defaultValue = "ALL") String state,
                                                            @RequestParam(name = "from",
                                                                          defaultValue = "0") Integer from,
                                                            @RequestParam(name = "size",
                                                                          defaultValue = "10") Integer size) {
        return bookingService.getAllUserItemsBookings(userId, state, validatePage(from, size));
    }

    private PageRequest validatePage(Integer from, Integer size) {
        if (from < 0 || size < 1) {
            throw new ValidationException("Параметры page нарушены: from=" + from + " size=" + size);
        } else {
            int page = from / size;
            return PageRequest.of(page, size);
        }
    }
}
