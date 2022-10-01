package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

interface BookingService {

    BookingDto create(BookingDto bookingDto, Long userId);

    BookingResponseDto update(Long id, Long userId, Boolean approved);

    BookingResponseDto getBooking(Long bookingId, Long userId);

    List<BookingResponseDto> getBookings(String state, Long userId, PageRequest pageRequest);

    List<BookingResponseDto> getOwnerBookings(String state, Long userId, PageRequest pageRequest);
}
