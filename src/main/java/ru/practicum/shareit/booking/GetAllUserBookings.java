package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("ALL")
public class GetAllUserBookings implements BookingGenerator {

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    @Autowired
    public GetAllUserBookings(UserRepository userRepository, BookingRepository bookingRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, PageRequest pageRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));
            List<Booking> bookings = bookingRepository.findAllByBooker(user, pageRequest);
            log.info("Возвращаем список всех бронирований для пользователя с id: {} {}", userId, bookings);
            return bookings.stream()
                    .map(BookingConverter::toBookingResponseDto)
                    .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, PageRequest pageRequest) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));
        Item item = itemRepository.findDistinctTopByOwner(owner);
        List<Booking> bookings = bookingRepository.findAllByItem(item, pageRequest);
        log.info("Возвращаем список всех бронирований для владельца с id: {} {}", userId, bookings);
        return bookings.stream()
                .map(BookingConverter::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}
