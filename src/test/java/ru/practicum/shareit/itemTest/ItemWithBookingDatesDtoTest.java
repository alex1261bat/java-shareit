package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.CommentResponseDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class ItemWithBookingDatesDtoTest {
    @Autowired
    private JacksonTester<ru.practicum.shareit.item.ItemWithBookingDatesDto> json;

    @Test
    void testItemDto() throws Exception {
        ru.practicum.shareit.item.ItemWithBookingDatesDto dto = new ru.practicum.shareit.item.ItemWithBookingDatesDto(
                1L,
                "name",
                "description",
                true,
                2L,
                new ru.practicum.shareit.item.ItemWithBookingDatesDto.BookingShortResponseDto(1L, 3L),
                new ru.practicum.shareit.item.ItemWithBookingDatesDto.BookingShortResponseDto(2L, 5L),
                List.of(new CommentResponseDto(1L, "text", "AuthorName", LocalDateTime.now().minusDays(1)))
        );

        JsonContent<ru.practicum.shareit.item.ItemWithBookingDatesDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments.size()").isEqualTo(1);
    }
}
