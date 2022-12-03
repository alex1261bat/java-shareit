package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User saveNewUser(@Valid @RequestBody User user) {
        return userService.add(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable Long userId, @RequestBody User user) {
        return userService.update(userId, user);
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {
        return userService.getById(userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
