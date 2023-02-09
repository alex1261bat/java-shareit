package ru.practicum.gateway.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody BookingRequestDto requestDto) {
        return bookingClient.saveNewBooking(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam(name = "approved") Boolean approved) {
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        return bookingClient.getBookingById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "state",
                                                                   defaultValue = "ALL") String state,
                                                     @RequestParam(name = "from",
                                                                   defaultValue = "0") Integer from,
                                                     @RequestParam(name = "size",
                                                                   defaultValue = "10") Integer size) {
        return bookingClient.getAllUserBookings(userId, State.getState(state), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllUserItemsBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(name = "state",
                                                                        defaultValue = "ALL") String state,
                                                          @RequestParam(name = "from",
                                                                        defaultValue = "0") Integer from,
                                                          @RequestParam(name = "size",
                                                                        defaultValue = "10") Integer size) {
        return bookingClient.getAllUserItemsBookings(userId, State.getState(state), from, size);
    }
}
