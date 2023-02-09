package ru.practicum.gateway.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.gateway.client.BaseClient;
import ru.practicum.gateway.pageValidator.PageValidator;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveNewBooking(Long userId, BookItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> approveBooking(Long bookingId, Long userId, Boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBookingById(Long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> getAllUserBookings(Long userId, State state, Integer from, Integer size) {
        PageValidator.validatePage(from, size);

        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "getState", from,
                "size", size
        );

        return get("?state={state}&getState={getState}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getAllUserItemsBookings(Long userId, State state, Integer from, Integer size) {
        PageValidator.validatePage(from, size);

        Map<String, Object> parameters = Map.of(
                "state", state.name(),
                "getState", from,
                "size", size
        );

        return get("/owner?state={state}&getState={getState}&size={size}", userId, parameters);
    }
}
