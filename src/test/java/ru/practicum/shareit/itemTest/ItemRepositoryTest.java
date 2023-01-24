package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private Item item1;
    private Item item2;
    private Item item3;
    private User user1;
    private ItemRequest request2;

    @BeforeEach
    void init() {
        user1 = userRepository.save(new User(1L, "name1", "email1@mail"));
        User user2 = userRepository.save(new User(2L, "name2", "email2@mail"));
        User user3 = userRepository.save(new User(3L, "name3", "email3@mail"));
        ItemRequest request1 = itemRequestRepository
                .save(new ItemRequest(1L, "requestDescription", user3, LocalDateTime.now()));
        request2 = itemRequestRepository
                .save(new ItemRequest(2L, "requestDescriptionNew", user3, LocalDateTime.now()));
        item1 = itemRepository
                .save(new Item(1L, "username1", "description1", false, user1, request1));
        item2 = itemRepository
                .save(new Item(2L, "username2", "description2", true, user2, request1));
        item3 = itemRepository
                .save(new Item(3L, "username3", "description3", true, user2, request2));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void getItemByIdTest() {
        final Item itemById = itemRepository.getItemById(item1.getId());
        assertEquals(item1.getId(), itemById.getId());
        assertEquals(item1.getName(), itemById.getName());
        assertEquals(item1.getDescription(), itemById.getDescription());
        assertEquals(item1.getAvailable(), itemById.getAvailable());
        assertEquals(item1.getOwner(), itemById.getOwner());
        assertEquals(item1.getRequest(), itemById.getRequest());
    }

    @Test
    void findAllByOwnerIdTest() {
        final List<Item> itemList = itemRepository.findAllByOwnerId(user1.getId(), Pageable.ofSize(5));
        assertEquals(1, itemList.size());
        assertEquals(item1.getId(), itemList.get(0).getId());
        assertEquals(item1.getName(), itemList.get(0).getName());
        assertEquals(item1.getDescription(), itemList.get(0).getDescription());
        assertEquals(item1.getAvailable(), itemList.get(0).getAvailable());
        assertEquals(item1.getOwner(), itemList.get(0).getOwner());
        assertEquals(item1.getRequest(), itemList.get(0).getRequest());
    }

    @Test
    void findAllByRequestIdTest() {
        final List<Item> itemList = itemRepository.findAvailableItems("name2", Pageable.unpaged());
        assertEquals(1, itemList.size());
        assertEquals(item2.getId(), itemList.get(0).getId());
        assertEquals(item2.getName(), itemList.get(0).getName());
        assertEquals(item2.getDescription(), itemList.get(0).getDescription());
        assertEquals(item2.getAvailable(), itemList.get(0).getAvailable());
        assertEquals(item2.getOwner(), itemList.get(0).getOwner());
        assertEquals(item2.getRequest(), itemList.get(0).getRequest());
    }

    @Test
    void findItemsByRequestIdTest() {
        final List<Item> itemList = itemRepository.findAllByRequestId(request2.getId());
        assertEquals(1, itemList.size());
        assertEquals(item3.getId(), itemList.get(0).getId());
        assertEquals(item3.getName(), itemList.get(0).getName());
        assertEquals(item3.getDescription(), itemList.get(0).getDescription());
        assertEquals(item3.getAvailable(), itemList.get(0).getAvailable());
        assertEquals(item3.getOwner(), itemList.get(0).getOwner());
        assertEquals(item3.getRequest(), itemList.get(0).getRequest());
    }
}
