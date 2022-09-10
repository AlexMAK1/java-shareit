package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component("CURRENT")
public class GetCurrentUserBookings implements BookingGenerator {

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    @Autowired
    public GetCurrentUserBookings(UserRepository userRepository, BookingRepository bookingRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, now, now);
        log.info("Возвращаем список текущих бронирований для пользователя с id: {} {}", userId, bookings);
        return bookings.stream()
                .map(BookingConverter::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));
        List<Item> items = itemRepository.findByOwner(owner);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner().getId().equals(userId)) {
                Booking booking = bookingRepository.findByItemAndStartBeforeAndEndAfter(item, now, now);
                if (booking != null) {
                    bookings.add(booking);

                }
            }
        }
        log.info("Возвращаем список текущих бронирований для владельца с id: {} {}", userId, bookings);
        return bookings.stream()
                .map(BookingConverter::toBookingResponseDto)
                .collect(Collectors.toList());
    }
}
