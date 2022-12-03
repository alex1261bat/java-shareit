package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    User add(User user);

    User update(Long id, User user);

    UserDto getById(Long id);

    List<UserDto> getAll();

    void delete(Long id);
}
