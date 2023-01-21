package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    Item item1;
    Item item2;
    Item item3;
    User user1;
    User user2;
    User user3;
    ItemRequest request1;
    ItemRequest request2;
    Booking booking1;
    Booking booking2;

    @BeforeEach
    void init() {
        user1 = userRepository.save(new User(1L, "name1", "email1@mail"));
        user2 = userRepository.save(new User(2L, "name2", "email2@mail"));
        user3 = userRepository.save(new User(3L, "name3", "email3@mail"));
        request1 = itemRequestRepository.save(new ItemRequest(1L, "requestDescription", user3,
                LocalDateTime.now()));
        request2 = itemRequestRepository.save(new ItemRequest(2L, "requestDescriptionNew", user3,
                LocalDateTime.now()));
        item1 = itemRepository.save(new Item(1L, "username1", "description1", false,
                user1, request1));
        item2 = itemRepository.save(new Item(2L, "username2", "description2", true,
                user2, request1));
        item3 = itemRepository.save(new Item(3L, "username3", "description3", true,
                user2, request2));
        booking1 = bookingRepository.save(new Booking(1L, LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(2), item1, user3, Status.WAITING));
        booking2 = bookingRepository.save(new Booking(2L, LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(4), item2, user3, Status.WAITING));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerIdOrderByStartDescTest() {
        final List<Booking> bookingList = bookingRepository.findAllByBookerIdOrderByStartDesc(user3.getId(),
                Pageable.unpaged());
        assertEquals(2, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem(), bookingList.get(0).getItem());
        assertEquals(booking1.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking1.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDescTest() {
        final List<Booking> bookingList = bookingRepository
                .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user3.getId(), LocalDateTime.now(),
                        LocalDateTime.now(), Pageable.unpaged());
        assertEquals(2, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem(), bookingList.get(0).getItem());
        assertEquals(booking1.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking1.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllItemOwnerBookingsTest() {
        final List<Booking> bookingList = bookingRepository.findAllItemOwnerBookings(user1.getId(), Pageable.unpaged());
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem(), bookingList.get(0).getItem());
        assertEquals(booking1.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking1.getStatus(), bookingList.get(0).getStatus());
        assertEquals(user1.getId(), bookingList.get(0).getItem().getOwner().getId());
    }

    @Test
    void findAllItemOwnerCurrentBookingsTest() {
        final List<Booking> bookingList = bookingRepository.findAllItemOwnerCurrentBookings(user1.getId(),
                LocalDateTime.now(), LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem(), bookingList.get(0).getItem());
        assertEquals(booking1.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking1.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllItemOwnerPastBookingsTest() {
        booking2.setEnd(LocalDateTime.now().minusDays(1));
        final List<Booking> bookingList = bookingRepository.findAllItemOwnerPastBookings(user2.getId(),
                LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
        assertEquals(booking2.getStart(), bookingList.get(0).getStart());
        assertEquals(booking2.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking2.getItem(), bookingList.get(0).getItem());
        assertEquals(booking2.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking2.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllItemOwnerFutureBookingsTest() {
        booking2.setEnd(LocalDateTime.now().plusDays(5));
        booking2.setStart(LocalDateTime.now().plusDays(4));
        final List<Booking> bookingList = bookingRepository.findAllItemOwnerFutureBookings(user2.getId(),
                LocalDateTime.now(), Pageable.unpaged());
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
        assertEquals(booking2.getStart(), bookingList.get(0).getStart());
        assertEquals(booking2.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking2.getItem(), bookingList.get(0).getItem());
        assertEquals(booking2.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking2.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllItemOwnerBookingsByStatusTest() {
        booking2.setStatus(Status.CANCELED);
        final List<Booking> bookingList = bookingRepository.findAllItemOwnerBookingsByStatus(user2.getId(),
                Status.CANCELED, Pageable.unpaged());
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
        assertEquals(booking2.getStart(), bookingList.get(0).getStart());
        assertEquals(booking2.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking2.getItem(), bookingList.get(0).getItem());
        assertEquals(booking2.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking2.getStatus(), bookingList.get(0).getStatus());

    }

    @Test
    void findAllByItemIdTest() {
        final List<Booking> bookingList = bookingRepository.findAllByItemId(item1.getId());
        assertEquals(1, bookingList.size());
        assertEquals(booking1.getId(), bookingList.get(0).getId());
        assertEquals(booking1.getStart(), bookingList.get(0).getStart());
        assertEquals(booking1.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking1.getItem(), bookingList.get(0).getItem());
        assertEquals(booking1.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking1.getStatus(), bookingList.get(0).getStatus());
    }

    @Test
    void findAllUserBookingsTest() {
        booking2.setEnd(LocalDateTime.now().minusDays(3));
        booking2.setStart(LocalDateTime.now().minusDays(4));
        final List<Booking> bookingList = bookingRepository.findAllUserBookings(user3.getId(), item2.getId(),
                LocalDateTime.now());
        assertEquals(1, bookingList.size());
        assertEquals(booking2.getId(), bookingList.get(0).getId());
        assertEquals(booking2.getStart(), bookingList.get(0).getStart());
        assertEquals(booking2.getEnd(), bookingList.get(0).getEnd());
        assertEquals(booking2.getItem(), bookingList.get(0).getItem());
        assertEquals(booking2.getBooker(), bookingList.get(0).getBooker());
        assertEquals(booking2.getStatus(), bookingList.get(0).getStatus());
    }

}
