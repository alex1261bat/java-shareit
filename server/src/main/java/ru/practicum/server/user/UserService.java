package ru.practicum.server.user;

import java.util.List;

public interface UserService {
    UserDto saveNewUser(UserDto userDto);

    UserDto update(Long id, UserDto userDto);

    UserDto getById(Long id);

    List<UserDto> getAll();

    void delete(Long id);
}
