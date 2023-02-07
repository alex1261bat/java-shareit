package ru.practicum.server.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.server.exceptions.NotFoundException;

public interface UserRepository extends JpaRepository<User, Long> {
    default User getUserById(Long userId) {
        return findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + userId + " не существует"));
    }
}
