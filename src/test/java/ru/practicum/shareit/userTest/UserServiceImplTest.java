package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    UserService userService;
    UserRepository userRepository;

    @BeforeEach
    void init() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void getAllTest() {
        User user = new User(1L, "name1", "email1@mail");
        User user1 = new User(2L, "name1", "email1@mail");
        List<User> userList = List.of(user, user1);

        when(userRepository.findAll()).thenReturn(userList);

        List<UserDto> userDtoList = userService.getAll();

        assertEquals(userList.size(), userDtoList.size());
        assertEquals(userList.get(0).getId(), userDtoList.get(0).getId());
        assertEquals(userList.get(0).getName(), userDtoList.get(0).getName());
        assertEquals(userList.get(0).getEmail(), userDtoList.get(0).getEmail());
    }

    @Test
    void getByIdTest() {
        User user = new User(1L, "name1", "email1@mail");

        when(userRepository.getUserById(anyLong())).thenReturn(user);

        UserDto userDto = userService.getById(user.getId());

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void saveNewUserTest() {
        UserDto userDto = new UserDto(1L, "name1", "email1@mail");

        when(userRepository.save(any())).thenReturn(UserMapper.toUser(userDto));

        UserDto newUserDto = userService.saveNewUser(userDto);

        assertEquals(userDto.getId(), newUserDto.getId());
        assertEquals(userDto.getName(), newUserDto.getName());
        assertEquals(userDto.getEmail(), newUserDto.getEmail());
    }

   @Test
    void updateTest() {
        UserDto userDto = new UserDto(1L, "UpdatedName1", "email1@mail");
        User user = new User(1L, "name1", "email1@mail");

        when(userRepository.getUserById(any())).thenReturn(user);
        when(userRepository.save(any())).thenReturn(UserMapper.toUser(userDto));

        UserDto updatedUserDto = userService.update(userDto.getId(), userDto);

        assertEquals(user.getId(), updatedUserDto.getId());
        assertEquals(user.getName(), updatedUserDto.getName());
        assertEquals(user.getEmail(), updatedUserDto.getEmail());
    }
}
