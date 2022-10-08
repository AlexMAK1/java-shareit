package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    @Autowired
    private Map<String, BookingGenerator> bookingGenerators;

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
            log.error("Вещь в данный момент не доступна: {}", item.getId());
            throw new ValidationException("Вещь в данный момент не доступна");
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
            log.info("Подтверждается запрос на бронирование: {}", id);
            return BookingConverter.toBookingResponseDto(booking);

        }
        booking.setStatus(Status.REJECTED);
        bookingRepository.save(booking);
        log.info("Отклоняется запрос на бронирование: {}", id);
        return BookingConverter.toBookingResponseDto(booking);

    }

    @Override
    public BookingResponseDto getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Брони " +
                "с таким id не существует"));
        Item item = itemRepository.getReferenceById(booking.getItem().getId());
        if (item.getOwner().getId().equals(userId)) {
            log.info("Находим бронирование с id: {}", bookingId);
            return BookingConverter.toBookingResponseDto(booking);
        }
        if (booking.getBooker().getId().equals(userId)) {
            log.info("Находим бронирование с id: {}", bookingId);
            return BookingConverter.toBookingResponseDto(booking);
        }
        log.error("Ошибка, валидация не пройдена. Пользователю с данным id вещь не принадлежит: {}", userId);
        throw new NotFoundException("Ошибка, валидация не пройдена. Пользователю с данным id " +
                "вещь не принадлежит");
    }

    @Override
    public List<BookingResponseDto> getBookings(String state, Long userId, PageRequest pageRequest) {
        BookingGenerator bookingUserGenerator = bookingGenerators.get(state);
        if (!bookingGenerators.containsKey(state)) {
            log.error("Unknown state: UNSUPPORTED_STATUS: {}", state);
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingUserGenerator.getUserBookings(userId, pageRequest);
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(String state, Long userId, PageRequest pageRequest) {
        BookingGenerator bookingOwnerGenerator = bookingGenerators.get(state);
        if (!bookingGenerators.containsKey(state)) {
            log.error("Unknown state: UNSUPPORTED_STATUS: {}", state);
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingOwnerGenerator.getOwnerBookings(userId, pageRequest);
    }
}
