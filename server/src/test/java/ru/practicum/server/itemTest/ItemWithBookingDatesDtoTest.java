package ru.practicum.server.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.server.item.CommentResponseDto;
import ru.practicum.server.item.ItemWithBookingDatesDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemWithBookingDatesDtoTest {
    private final JacksonTester<ru.practicum.server.item.ItemWithBookingDatesDto> json;

    @Test
    void testItemDto() throws Exception {
        ItemWithBookingDatesDto dto = new ItemWithBookingDatesDto(
                1L,
                "name",
                "description",
                true,
                2L,
                new ItemWithBookingDatesDto.BookingShortResponseDto(1L, 3L),
                new ItemWithBookingDatesDto.BookingShortResponseDto(2L, 5L),
                List.of(new CommentResponseDto(1L, "text", "AuthorName", LocalDateTime.now()
                        .minusDays(1)))
        );

        JsonContent<ItemWithBookingDatesDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.comments.size()").isEqualTo(1);
    }
}
