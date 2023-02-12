package ru.practicum.gateway.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.exceptions.ValidationException;
import ru.practicum.gateway.pageValidator.PageValidator;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> saveNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @Valid @RequestBody BookingRequestDto bookingRequestDto) {

        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new ValidationException("Время начала бронирования не может быть позже времени окончания");
        }

        log.info("Получен запрос на создание бронирования вещи с id=" + bookingRequestDto.getItemId()
                + " от пользователя с id=" + userId);
        return bookingClient.saveNewBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId,
                                                 @RequestParam(name = "approved") Boolean approved) {
        log.info("Получен запрос на одобрение бронирования с id=" + bookingId + " от пользователя с id=" + userId);
        return bookingClient.approveBooking(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long bookingId) {
        log.info("Получен запрос на получение бронирования с id=" + bookingId + " от пользователя с id=" + userId);
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
        PageValidator.validatePage(from, size);
        log.info("Получен запрос на получение бронирований с state=" + state + " параметрами from=" + from + "size="
                + size + " от пользователя с id=" + userId);
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
        PageValidator.validatePage(from, size);
        log.info("Получен запрос на получение бронирований вещи с state=" + state + " параметрами from=" + from + "size="
                + size + " владельцем с id=" + userId);
        return bookingClient.getAllUserItemsBookings(userId, State.getState(state), from, size);
    }
}
