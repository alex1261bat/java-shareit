package ru.practicum.server.userTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.server.user.UserController;
import ru.practicum.server.user.UserDto;
import ru.practicum.server.user.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    @MockBean
    private final UserService userService;
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final UserDto userDto = new UserDto(1L, "name", "email@mail");

    @Test
    void saveNewUserTest() throws Exception {
        when(userService.saveNewUser(any())).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                        .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                        .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
        verify(userService, times(1)).saveNewUser(userDto);
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.update(anyLong(), any())).thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                        .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                        .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
        verify(userService, times(1)).update(1L, userDto);
    }

    @Test
    void findUserByIDTest() throws Exception {
        when(userService.getById(anyLong())).thenReturn(userDto);

        mockMvc.perform(get("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                        .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class))
                        .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class));
        verify(userService, times(1)).getById(1L);
    }

    @Test
    void getAllUsersTest() throws Exception {

        UserDto userDto1 = new UserDto(1L, "name1", "email1@mail");
        UserDto userDto2 = new UserDto(2L, "name2", "email2@mail");
        List<UserDto> userList = List.of(userDto, userDto1, userDto2);
        when(userService.getAll()).thenReturn(userList);

        mockMvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$", hasSize(3)));
        verify(userService, times(1)).getAll();
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        doNothing().when(userService).delete(anyLong());

        mockMvc.perform(delete("/users/1")
                        .header("X-Sharer-User-Id", 1L))
                        .andExpect(status().isOk());
        verify(userService, times(1)).delete(1L);
    }
}
