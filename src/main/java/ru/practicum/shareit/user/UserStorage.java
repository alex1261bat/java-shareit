package ru.practicum.shareit.user;

import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

public interface UserStorage {
    User add(User user);

    User update(Long id, User user);

    User getById(Long id) throws NotFoundException;

    List<User> getAll();

    void delete(Long id);
}
