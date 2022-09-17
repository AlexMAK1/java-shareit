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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("REJECTED")
public class GetRejectedUserBookings implements BookingGenerator {

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private final ItemRepository itemRepository;

    @Autowired
    public GetRejectedUserBookings(UserRepository userRepository, BookingRepository bookingRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Long userId, PageRequest pageRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));
        Status status = Status.REJECTED;
        List<Booking> bookings = bookingRepository.findAllByBookerAndStatusEquals(user, status);
        log.info("Возвращаем список отклоненных бронирований для пользователя с id: {} {}", userId, bookings);
        return bookings.stream()
                .sorted(this::compareAll)
                .map(BookingConverter::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Long userId, PageRequest pageRequest) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));
        List<Item> items = itemRepository.findByOwner(owner);
        Status status = Status.REJECTED;
        List<Booking> bookings = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner().getId().equals(userId)) {
                Booking booking = bookingRepository.findByItemAndStatusEquals(item, status);
                if (booking != null) {
                    bookings.add(booking);
                }
            }
        }
        log.info("Возвращаем список отклонненых бронирований для владельца с id: {} {}", userId, bookings);
        return bookings.stream()
                .sorted(this::compareAll)
                .map(BookingConverter::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    private int compareAll(Booking booking1, Booking booking2) {
        return booking2.getEnd().compareTo(booking1.getEnd());
    }
}

