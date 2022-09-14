package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;

interface BookingService {

    BookingDto create(BookingDto bookingDto, Long userId) throws ValidationException;

    BookingResponseDto update(Long id, Long userId, Boolean approved);

    BookingResponseDto getBooking(Long bookingId, Long userId);

    List<BookingResponseDto> getBookings(String state, Long userId);

    List<BookingResponseDto> getOwnerBookings(String state, Long userId);
}
