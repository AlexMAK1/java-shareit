package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingGenerator {

    List<BookingResponseDto> getUserBookings(Long userId, PageRequest pageRequest);

    List<BookingResponseDto> getOwnerBookings(Long userId, PageRequest pageRequest);
}
