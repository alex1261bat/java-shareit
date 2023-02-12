package ru.practicum.server.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.server.booking.Booking;
import ru.practicum.server.booking.BookingRequestDto;
import ru.practicum.server.booking.BookingService;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final EntityManager entityManager;
    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Test
    void saveNewBookingTest() {
        User user = userRepository.save(new User(1L, "name1", "email1@mail"));
        User booker = userRepository.save(new User(30L, "booker", "booker@mail"));
        Item item = itemRepository.save(new Item(1L, "username1", "description1", true,
                user, null));
        BookingRequestDto bookingDto = makeBookingDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId());
        bookingService.saveNewBooking(booker.getId(), bookingDto);

        TypedQuery<Booking> query = entityManager.createQuery("Select b from Booking b where b.item.id = :itemId",
                Booking.class);
        Booking booking = query.setParameter("itemId", bookingDto.getItemId()).getSingleResult();

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getBooker(), equalTo(booker));
    }

   private BookingRequestDto makeBookingDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);

        return dto;
    }
}
