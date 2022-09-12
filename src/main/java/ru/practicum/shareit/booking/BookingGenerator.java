package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingGenerator {

    List<BookingResponseDto> getUserBookings(Long userId);

    List<BookingResponseDto> getOwnerBookings(Long userId);
}
