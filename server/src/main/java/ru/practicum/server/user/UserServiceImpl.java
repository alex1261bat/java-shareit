package ru.practicum.server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserDto saveNewUser(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.getUserById(id);

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }

        userRepository.save(user);

        return UserMapper.toUserDto(user);
    }

    public UserDto getById(Long id) {
        return UserMapper.toUserDto(userRepository.getUserById(id));
    }

    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
