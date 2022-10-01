package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingResponseDtoTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    void testSerialize() throws Exception {
        User user1 = new User(1L, "User1", "user1@mail");
        Item item1 = new Item("item1", "table", true, user1, 1L);
        LocalDateTime start = LocalDateTime.of(2022, 10, 23, 8, 0);
        LocalDateTime end = LocalDateTime.of(2022, 10, 23, 9, 0);
        BookingResponseDto bookingResponseDto = new BookingResponseDto(1L, start, end, Status.WAITING, item1, user1);

        JsonContent<BookingResponseDto> result = this.json.write(bookingResponseDto);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2022-10-23T08:00:00");
        assertThat(result).doesNotHaveJsonPath("$.enabled");
    }
}
