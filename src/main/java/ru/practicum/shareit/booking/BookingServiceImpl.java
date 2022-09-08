package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingDto create(BookingDto bookingDto, Long userId) {
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFoundException("Вещи " +
                "с таким id не существует"));
        if (item.getOwner().getId().equals(userId)) {
            log.error("Владелец не может создавать бронь для своей же вещи: {}", userId);
            throw new NotFoundException("Владелец не может создавать бронь для своей же вещи");
        }
        if (!item.getAvailable()) {
            log.error("Вещь в данный момент не доступна");
            throw new ValidationException("Вещь в данный момент не доступна");
        }
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            log.error("Дата окончания брони перед датой начала брони");
            throw new ValidationException("Дата окончания брони перед датой начала брони");
        }

        User booker = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));

        Booking booking = BookingConverter.toBooking(bookingDto, item, booker);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        log.info("Сохраняем новое бронирование: {}", booking);
        return BookingConverter.toBookingDto(booking, bookingDto.getItemId());
    }

    @Override
    public BookingResponseDto update(Long id, Long userId, Boolean approved) {
        Booking booking = bookingRepository.getReferenceById(id);
        Item item = itemRepository.getReferenceById(booking.getItem().getId());
        if (approved && booking.getStatus().equals(Status.APPROVED)) {
            log.error("Данное бронирование уже подтверждено: {}", id);
            throw new ValidationException("Данное бронирование уже подтверждено");
        }
        if (!item.getOwner().getId().equals(userId)) {
            log.error("Ошибка, валидация не пройдена. Пользователю с данным id вещь не принаджит: {}", userId);
            throw new NotFoundException("Ошибка, валидация не пройдена. Пользователю с данным id " +
                    "вещь не принаджит");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
            bookingRepository.save(booking);
            log.info("Подтверждается запрос на бронирование: {}", booking);
            return BookingConverter.toBookingResponseDto(booking);

        }
        booking.setStatus(Status.REJECTED);
        bookingRepository.save(booking);
        log.info("Отклоняется запрос на бронирование: {}", booking);
        return BookingConverter.toBookingResponseDto(booking);

    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Брони " +
                "с таким id не существует"));
        Item item = itemRepository.getReferenceById(booking.getItem().getId());
        if (item.getOwner().getId().equals(userId)) {
            log.info("Находим бронирование с id: {}", booking);
            return BookingConverter.toBookingResponseDto(booking);
        }
        if (booking.getBooker().getId().equals(userId)) {
            log.info("Находим бронирование с id: {}", booking);
            return BookingConverter.toBookingResponseDto(booking);
        }
        log.error("Ошибка, валидация не пройдена. Пользователю с данным id вещь не принаджит: {}", userId);
        throw new NotFoundException("Ошибка, валидация не пройдена. Пользователю с данным id " +
                "вещь не принаджит");
    }


    @Override
    public List<BookingResponseDto> getBookings(String state, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));
        if (state.equals("ALL")) {
            List<Booking> bookings = bookingRepository.findAllByBooker(user);
            log.info("Возвращаем список всех бронирований для пользователя с id: {} {}", userId, bookings);
            return bookings.stream()
                    .sorted(this::compareAll)
                    .map(BookingConverter::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            List<Booking> bookings = bookingRepository.findAllByBooker(user);
            log.info("Возвращаем список будущих бронирований для пользователя с id: {} {}", userId, bookings);
            return bookings.stream()
                    .sorted(this::compareFuture)
                    .map(BookingConverter::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("WAITING")) {
            Status status = Status.WAITING;
            List<Booking> bookings = bookingRepository.findAllByBookerAndStatusEquals(user, status);
            log.info("Возвращаем список ожидающих бронирований для пользователя с id: {} {}", userId, bookings);
            return bookings.stream()
                    .sorted(this::compareAll)
                    .map(BookingConverter::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("REJECTED")) {
            Status status = Status.REJECTED;
            List<Booking> bookings = bookingRepository.findAllByBookerAndStatusEquals(user, status);
            log.info("Возвращаем список отклоненных бронирований для пользователя с id: {} {}", userId, bookings);
            return bookings.stream()
                    .sorted(this::compareAll)
                    .map(BookingConverter::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("CURRENT")) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, now, now);
            log.info("Возвращаем список текущих бронирований для пользователя с id: {} {}", userId, bookings);
            return bookings.stream()
                    .map(BookingConverter::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("PAST")) {
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingRepository.findAllByBookerAndEndBefore(user, now);
            log.info("Возвращаем список прошедших бронирований для пользователя с id: {} {}", userId, bookings);
            return bookings.stream()
                    .map(BookingConverter::toBookingResponseDto)
                    .collect(Collectors.toList());
        }

        log.error("Unknown state: UNSUPPORTED_STATUS: {}", state);
        throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(String state, Long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));
        if (state.equals("ALL")) {
            Item item = itemRepository.findDistinctTopByOwner(owner);
            List<Booking> bookings = bookingRepository.findAllByItem(item);
            log.info("Возвращаем список всех бронирований для владельца с id: {} {}", userId, bookings);
            return bookings.stream()
                    .sorted(this::compareAll)
                    .map(BookingConverter::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("FUTURE")) {
            Item item = itemRepository.findDistinctTopByOwner(owner);
            List<Booking> bookings = bookingRepository.findAllByItem(item);
            log.info("Возвращаем список будущих бронирований для владельца с id: {} {}", userId, bookings);
            return bookings.stream()
                    .sorted(this::compareFuture)
                    .map(BookingConverter::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        if (state.equals("WAITING")) {
            List<Item> items = itemRepository.findByOwner(owner);
            Status status = Status.WAITING;
            List<Booking> bookings = new ArrayList<>();
            for (Item item : items) {
                if (item.getOwner().getId().equals(userId)) {
                    Booking booking = bookingRepository.findByItemAndStatusEquals(item, status);
                    if (booking != null) {
                        bookings.add(booking);
                        log.info("Возвращаем список отклонненых бронирований для владельца с id: {} {}", userId, bookings);
                        return bookings.stream()
                                .sorted(this::compareAll)
                                .map(BookingConverter::toBookingResponseDto)
                                .collect(Collectors.toList());
                    }
                }
            }
        }
        if (state.equals("REJECTED")) {
            List<Item> items = itemRepository.findByOwner(owner);
            Status status = Status.REJECTED;
            List<Booking> bookings = new ArrayList<>();
            for (Item item : items) {
                if (item.getOwner().getId().equals(userId)) {
                    Booking booking = bookingRepository.findByItemAndStatusEquals(item, status);
                    if (booking != null) {
                        bookings.add(booking);
                        log.info("Возвращаем список отклонненых бронирований для владельца с id: {} {}", userId, bookings);
                        return bookings.stream()
                                .sorted(this::compareAll)
                                .map(BookingConverter::toBookingResponseDto)
                                .collect(Collectors.toList());
                    }
                }
            }
        }
        if (state.equals("CURRENT")) {
            List<Item> items = itemRepository.findByOwner(owner);
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = new ArrayList<>();
            for (Item item : items) {
                if (item.getOwner().getId().equals(userId)) {
                    Booking booking = bookingRepository.findByItemAndStartBeforeAndEndAfter(item, now, now);
                    if (booking != null) {
                        bookings.add(booking);
                        log.info("Возвращаем список текущих бронирований для владельца с id: {} {}", userId, bookings);
                        return bookings.stream()
                                .map(BookingConverter::toBookingResponseDto)
                                .collect(Collectors.toList());
                    }
                }
            }
        }
        if (state.equals("PAST")) {
            Item item = itemRepository.findDistinctTopByOwner(owner);
            LocalDateTime now = LocalDateTime.now();
            List<Booking> bookings = bookingRepository.findAllByItemAndEndBefore(item, now);
            log.info("Возвращаем список прошедших бронирований для владельца с id: {} {}", userId, bookings);
            return bookings.stream()
                    .map(BookingConverter::toBookingResponseDto)
                    .collect(Collectors.toList());
        }
        log.error("Unknown state: UNSUPPORTED_STATUS: {}", state);
        throw new
                ValidationException("Unknown state: UNSUPPORTED_STATUS");
    }

    private int compareAll(Booking booking1, Booking booking2) {
        return booking2.getEnd().compareTo(booking1.getEnd());
    }

    private int compareFuture(Booking booking1, Booking booking2) {
        LocalDateTime currentMoment = LocalDateTime.now();
        return (booking2.getEnd().compareTo(currentMoment)) - (booking1.getEnd().compareTo(currentMoment));
    }
}
