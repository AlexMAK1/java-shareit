package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetFutureUserBookingsTest {

    User user1 = new User(1L, "User1", "user1@mail");

    Item item1 = new Item("item1", "table", true, user1, 1L);
    LocalDateTime start = LocalDateTime.of(2022, 10, 23, 8, 00);
    LocalDateTime end = LocalDateTime.of(2022, 10, 23, 9, 00);

    Booking booking = new Booking(start, end, item1, user1);

    BookingGenerator bookingGenerator;
    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        bookingRepository = mock(BookingRepository.class);

        bookingGenerator = new GetFutureUserBookings(userRepository, bookingRepository, itemRepository);
    }

    @Test
    void getUserBookings() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(bookingRepository.findAllByBooker(any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> bookingResponseDtoList = bookingGenerator.getUserBookings(1L, PageRequest.ofSize(10));

        assertNotNull(bookingResponseDtoList);
    }

    @Test
    void getOwnerBookings() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user1));
        when(itemRepository.findDistinctTopByOwner(any()))
                .thenReturn(item1);
        when(bookingRepository.findAllByItem(any(), any()))
                .thenReturn(List.of(booking));

        List<BookingResponseDto> bookingResponseDtoList = bookingGenerator.getOwnerBookings(1L, PageRequest.ofSize(10));

        assertNotNull(bookingResponseDtoList);
    }
}