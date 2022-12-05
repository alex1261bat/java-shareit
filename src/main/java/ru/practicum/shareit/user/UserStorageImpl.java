package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
@Setter
@Getter
public class UserStorageImpl implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();
    private long userId = 0;

    @Override
    public User add(User user) {
        validateUserEmail(user);
        user.setId(getId());
        users.put(user.getId(), user);
        log.trace("Сохранен объект: {}", user);
        return users.get(user.getId());
    }

    @Override
    public User update(Long id, User user) {
        getById(id);
        User storedUser = users.get(id);
        if (user.getName() != null) {
            storedUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            validateUserEmail(user);
            storedUser.setEmail(user.getEmail());
        }

        users.put(id, storedUser);
        log.trace("Обновлен объект: {}.", storedUser);
        return users.get(id);
    }

    @Override
    public User getById(Long id) throws NotFoundException {
        if (users.get(id) == null) {
            throw new NotFoundException("Пользователь с id=" + id + " не существует");
        } else {
            return users.get(id);
        }
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(Long id) {
        User user = getById(id);
        users.remove(id);
        log.trace("Удален объект: {}.", user);
    }

    private long getId() {
        setUserId(userId + 1);
        return userId;
    }

    private void validateUserEmail(User user) {
        for (User element : users.values()) {
            if (element.getEmail().equals(user.getEmail())) {
                throw new ValidationException("Пользователь с такой почтой уже существует");
            }
        }
    }
}
