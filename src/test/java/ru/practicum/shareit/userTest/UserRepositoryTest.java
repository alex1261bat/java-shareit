package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @Test
    void getUserByIdTest() {
        User user1 = userRepository.save(new User(1L, "name1", "email1@mail"));
        final Optional<User> user = userRepository.findById(user1.getId());
        assertEquals(user1.getId(), user.get().getId());
        assertEquals(user1.getName(), user.get().getName());
        assertEquals(user1.getEmail(), user.get().getEmail());
        userRepository.deleteAll();
    }
}