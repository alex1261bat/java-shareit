package ru.practicum.shareit.bookingTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.pageValidator.PageValidator;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    @MockBean
    private final BookingService bookingService;
    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final User user1 = new User(1L, "name1", "email1@mail");
    private final Item item = new Item(1L, "itemName", "itemDescription", true, user1,
            null);
    private final BookingResponseDto bookingDto = new BookingResponseDto(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2), ItemMapper.toItemDto(item), Status.WAITING, UserMapper.toUserDto(user1));
    private final BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(2), item.getId());

    @Test
    void saveNewBookingTest() throws Exception {
        when(bookingService.saveNewBooking(anyLong(), any())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
        verify(bookingService, times(1)).saveNewBooking(1L, bookingRequestDto);
    }

    @Test
    void approveBookingTest() throws Exception {
        BookingResponseDto bookingDtoApproved = new BookingResponseDto(1L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), ItemMapper.toItemDto(item), Status.APPROVED,
                UserMapper.toUserDto(user1));

        when(bookingService.approveBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoApproved);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id", is(bookingDtoApproved.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoApproved.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.id", is(bookingDtoApproved.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingDtoApproved.getStatus().toString()), Status.class));
        verify(bookingService, times(1)).approveBooking(1L, bookingRequestDto.getId(),
                true);
    }

    @Test
    void getBookingByIdTest() throws Exception {
        when(bookingService.getById(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class));
        verify(bookingService, times(1)).getById(1L, bookingRequestDto.getId());
    }

    @Test
    void getAllUserItemsBookingsTest() throws Exception {
        when(bookingService.getAllUserBookings(anyLong(), anyString(), any()))
                .thenReturn(Collections.singletonList(bookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "0")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookingService, times(1))
                .getAllUserItemsBookings(1L, "ALL", PageValidator.validatePage(0, 5));
    }

    @Test
    void getAllUserBookingsTest() throws Exception {
        User user2 = new User(2L, "name2", "email2@mail");
        BookingResponseDto bookingDto2 = new BookingResponseDto(2L, LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2), ItemMapper.toItemDto(item), Status.WAITING,
                UserMapper.toUserDto(user2));

        when(bookingService.getAllUserItemsBookings(anyLong(), anyString(), any()))
                .thenReturn(List.of(bookingDto, bookingDto2));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "0")
                        .param("state", "ALL")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookingService, times(1))
                .getAllUserBookings(1L, "ALL", PageValidator.validatePage(0, 5));
    }
}
