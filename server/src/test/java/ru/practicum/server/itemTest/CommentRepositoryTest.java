package ru.practicum.server.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.server.item.Comment;
import ru.practicum.server.item.CommentRepository;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemRepository;
import ru.practicum.server.request.ItemRequest;
import ru.practicum.server.request.ItemRequestRepository;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentRepositoryTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;
    private Item item2;
    private Comment comment2;

    @BeforeEach
    void init() {
        User user2 = userRepository.save(new User(2L, "name2", "email2@mail"));
        User user3 = userRepository.save(new User(3L, "name3", "email3@mail"));
        ItemRequest request1 = itemRequestRepository
                .save(new ItemRequest(1L, "requestDescription", user3, LocalDateTime.now()));
        item2 = itemRepository
                .save(new Item(2L, "username2", "description2", true, user2, request1));
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
