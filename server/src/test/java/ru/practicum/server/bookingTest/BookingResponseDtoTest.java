package ru.practicum.server.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.server.booking.BookingResponseDto;
import ru.practicum.server.booking.Status;
import ru.practicum.server.item.Item;
import ru.practicum.server.item.ItemMapper;
import ru.practicum.server.user.User;
import ru.practicum.server.user.UserMapper;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingResponseDtoTest {
    private final JacksonTester<BookingResponseDto> json;

    @Test
    void testBookingDto() throws Exception {
        User user1 = new User(1L, "name1", "email1@mail");
        User booker = new User(1L, "booker", "booker@mail");
        Item item = new Item(1L, "itemName", "itemDescription", true, user1, null);
        BookingResponseDto bookingDto = new BookingResponseDto(
                1L,
                LocalDateTime.of(2022,9,10,11,12,13).plusDays(1),
                LocalDateTime.of(2022,9,10,11,12,13).plusDays(2),
                ItemMapper.toItemDto(item),
                Status.WAITING,
                UserMapper.toUserDto(booker)
        );

        JsonContent<BookingResponseDto> result = json.write(bookingDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(bookingDto.getStart().toString());
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(bookingDto.getEnd().toString());
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
    }
}
