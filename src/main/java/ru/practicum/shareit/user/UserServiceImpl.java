package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public User add(User user) {
        return userStorage.add(user);
    }

    public User update(Long id, User user) {
        return userStorage.update(id, user);
    }

    public UserDto getById(Long id) {
        return userStorage.getById(id);
    }

    public List<UserDto> getAll() {
        return userStorage.getAll();
    }

    public void delete(Long id) {
        userStorage.delete(id);
    }
}
