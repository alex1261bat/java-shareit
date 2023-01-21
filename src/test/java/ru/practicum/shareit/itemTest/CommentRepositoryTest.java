package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    CommentRepository commentRepository;
    Item item1;
    Item item2;
    Item item3;
    User user1;
    User user2;
    User user3;
    ItemRequest request1;
    ItemRequest request2;
    Comment comment1;
    Comment comment2;

    @BeforeEach
    void init() {
        user1 = userRepository.save(new User(1L, "name1", "email1@mail"));
        user2 = userRepository.save(new User(2L, "name2", "email2@mail"));
        user3 = userRepository.save(new User(3L, "name3", "email3@mail"));
        request1 = itemRequestRepository
                .save(new ItemRequest(1L, "requestDescription", user3, LocalDateTime.now()));
        request2 = itemRequestRepository
                .save(new ItemRequest(2L, "requestDescriptionNew", user3, LocalDateTime.now()));
        item1 = itemRepository
                .save(new Item(1L, "username1", "description1", false, user1, request1));
        item2 = itemRepository
                .save(new Item(2L, "username2", "description2", true, user2, request1));
        item3 = itemRepository
                .save(new Item(3L, "username3", "description3", true, user2, request2));
        comment1 = commentRepository
                .save(new Comment(1L, "text", item1, user3, LocalDateTime.now()));
        comment2 = commentRepository
                .save(new Comment(2L, "text1", item2, user3, LocalDateTime.now()));
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findAllByItemIdTest() {
        final List<Comment> commentList = commentRepository.findAllByItemId(item2.getId());
        assertEquals(1, commentList.size());
        assertEquals(item2.getId(), commentList.get(0).getItem().getId());
        assertEquals(comment2.getId(), commentList.get(0).getId());
        assertEquals(comment2.getText(), commentList.get(0).getText());
    }
}
