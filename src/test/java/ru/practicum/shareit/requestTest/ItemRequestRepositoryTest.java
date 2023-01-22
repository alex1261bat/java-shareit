package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    User user1;
    User user2;
    User user3;
    ItemRequest itemRequest1;
    ItemRequest itemRequest2;

    @BeforeEach
    void init() {
        user1 = userRepository.save(new User(1L, "name1", "email1@mail"));
        user2 = userRepository.save(new User(2L, "name2", "email2@mail"));
        user3 = userRepository.save(new User(3L, "name3", "email3@mail"));
        itemRequest1 = itemRequestRepository.save(new ItemRequest(1L, "requestDescription", user2,
                LocalDateTime.now()));
        itemRequest2 = itemRequestRepository.save(new ItemRequest(2L, "requestDescriptionNew", user3,
                LocalDateTime.now()));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findItemRequestByRequestorIdTest() {
        final List<ItemRequest> requestList = itemRequestRepository
                .findAllByRequestorIdOrderByCreatedDesc(user3.getId());
        assertEquals(1, requestList.size());
        assertEquals(itemRequest2.getId(), requestList.get(0).getId());
        assertEquals(itemRequest2.getDescription(), requestList.get(0).getDescription());
        assertEquals(itemRequest2.getRequestor(), requestList.get(0).getRequestor());
        assertEquals(itemRequest2.getCreated(), requestList.get(0).getCreated());
        assertEquals(user3.getId(), requestList.get(0).getRequestor().getId());
        assertEquals(user3.getName(), requestList.get(0).getRequestor().getName());
    }

    @Test
    void findAllByOtherUserTest() {
        final List<ItemRequest> requestList = itemRequestRepository
                .findAllByOtherUser(user2.getId(), Pageable.unpaged());
        assertEquals(1, requestList.size());
        assertEquals(itemRequest2.getId(), requestList.get(0).getId());
        assertEquals(itemRequest2.getDescription(), requestList.get(0).getDescription());
        assertEquals(itemRequest2.getRequestor(), requestList.get(0).getRequestor());
        assertEquals(itemRequest2.getCreated(), requestList.get(0).getCreated());
        assertEquals(user3.getId(), requestList.get(0).getRequestor().getId());
        assertEquals(user3.getName(), requestList.get(0).getRequestor().getName());
    }

    @Test
    void getItemRequestByIdTest() {
        final ItemRequest itemRequest = itemRequestRepository.getItemRequestById(itemRequest1.getId());
        assertEquals(itemRequest1.getId(), itemRequest.getId());
        assertEquals(itemRequest1.getDescription(), itemRequest.getDescription());
        assertEquals(itemRequest1.getRequestor(), itemRequest.getRequestor());
    }
}
