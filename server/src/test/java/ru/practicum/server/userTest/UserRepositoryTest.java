package ru.practicum.server.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    private final UserRepository userRepository;

    @Test
    void getUserByIdTest() {
        User user1 = userRepository.save(new User(1L, "name1", "email1@mail"));
        final User user = userRepository.getUserById(user1.getId());
        assertEquals(user1.getId(), user.getId());
        assertEquals(user1.getName(), user.getName());
        assertEquals(user1.getEmail(), user.getEmail());
        userRepository.deleteAll();
    }
}
