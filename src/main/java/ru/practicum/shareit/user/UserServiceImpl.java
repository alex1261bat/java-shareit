package ru.practicum.shareit.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Getter
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserDto add(UserDto userDto) {
        return UserMapper.toUserDto(userStorage.add(UserMapper.toUser(userDto)));
    }

    public UserDto update(Long id, UserDto userDto) {
        return UserMapper.toUserDto(userStorage.update(id, UserMapper.toUser(userDto)));
    }

    public UserDto getById(Long id) {
        return UserMapper.toUserDto(userStorage.getById(id));
    }

    public List<UserDto> getAll() {
        return userStorage.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public void delete(Long id) {
        userStorage.delete(id);
    }
}
