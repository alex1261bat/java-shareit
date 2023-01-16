package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public UserDto saveNewUser(UserDto userDto) {
        return UserMapper.toUserDto(userStorage.save(UserMapper.toUser(userDto)));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = UserMapper.toUser(getById(id));

        validateUserEmail(UserMapper.toUser(userDto));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        userStorage.save(user);

        return UserMapper.toUserDto(user);
    }

    public UserDto getById(Long id) {
        return UserMapper.toUserDto(userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с id=" + id + " не существует")));
    }

    public List<UserDto> getAll() {
        return userStorage.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public void delete(Long id) {
        userStorage.deleteById(id);
    }

    private void validateUserEmail(User user) {
        List<User> userList = userStorage.findAll().stream()
                .filter(userInList -> userInList.getEmail().equals(user.getEmail())).collect(Collectors.toList());
        if (userList.size() > 0) {
            throw new ValidationException("email уже занят");
        }
    }
}
