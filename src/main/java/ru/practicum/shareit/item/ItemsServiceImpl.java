package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ItemsServiceImpl implements ItemService {

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    private final CommentRepository commentRepository;

    @Autowired
    public ItemsServiceImpl(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));
        Item item = ItemConverter.toItem(itemDto, owner);
        itemRepository.save(item);
        log.info("Сохраняем новую вещь: {}", item);
        return ItemConverter.toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, long userId, long id) {
        validate(userId, id);
        Item item = itemRepository.getReferenceById(id);
        String name = itemDto.getName();
        if (name != null) {
            item.setName(name);
        }
        String description = itemDto.getDescription();
        if (description != null) {
            item.setDescription(description);
        }
        Boolean available = itemDto.getAvailable();
        if (available != null) {
            item.setAvailable(available);
        }
        log.info("Обновляем вещь: {}", item);
        return ItemConverter.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemInfoDto getItem(long id, long userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id" +
                " не существует"));
        Item item = itemRepository.findById(id).orElseThrow(() -> new NotFoundException("Вещи с таким id" +
                " не существует"));
        LocalDateTime now = LocalDateTime.now();
        Booking bookingLast = bookingRepository.findByItemAndAndEndBefore(item, now);
        Booking bookingNext = bookingRepository.findByItemAndAndEndAfter(item, now);
        Comment comment = commentRepository.findByItem(item);
        if (bookingLast == null || bookingNext == null || !item.getOwner().getId().equals(userId) && comment == null) {
            log.info("Находим вещь с id: {} {}", id, item);
            List<CommentDto> comments = new ArrayList<>();
            return ItemConverter.toItemInfoDto(item, null, null, comments);
        } else if (!item.getOwner().getId().equals(userId) && comment != null) {
            List<Comment> comments = commentRepository.findAllByItem(item);
            log.info("Находим вещь с id: {} {}", id, item);
            return ItemConverter.toItemInfoDto(item, null, null,
                    comments.stream().map(ItemConverter::toCommentDto).collect(Collectors.toList()));
        } else {
            List<Comment> comments = commentRepository.findAllByItem(item);
            ItemInfoDto.BookingDto lastBooking = new ItemInfoDto.BookingDto(bookingLast.getId(), bookingLast.getBooker().getId());
            ItemInfoDto.BookingDto nextBooking = new ItemInfoDto.BookingDto(bookingNext.getId(), bookingNext.getBooker().getId());
            log.info("Находим вещь с id: {} {}", id, item);
            return ItemConverter.toItemInfoDto(item, lastBooking, nextBooking,
                    comments.stream().map(ItemConverter::toCommentDto).collect(Collectors.toList()));
        }
    }

    @Override
    public List<ItemInfoDto> getItems(long userId) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователя с таким id " +
                "не существует"));
        List<Item> items = itemRepository.findItemsByOwner(owner);
        List<ItemInfoDto> itemInfoDtos = new ArrayList<>();
        for (Item item : items) {
            LocalDateTime now = LocalDateTime.now();
            Booking bookingLast = bookingRepository.findByItemAndAndEndBefore(item, now);
            Booking bookingNext = bookingRepository.findByItemAndAndEndAfter(item, now);
            if (bookingLast == null || bookingNext == null) {
                ItemInfoDto itemInfoDto = ItemConverter.toItemInfoDto(item, null, null, null);
                itemInfoDtos.add(itemInfoDto);
            } else {
                List<Comment> comments = commentRepository.findAllByItem(item);
                ItemInfoDto.BookingDto lastBooking = new ItemInfoDto.BookingDto(bookingLast.getId(), bookingLast.getBooker().getId());
                ItemInfoDto.BookingDto nextBooking = new ItemInfoDto.BookingDto(bookingNext.getId(), bookingNext.getBooker().getId());
                ItemInfoDto itemInfoDto = ItemConverter.toItemInfoDto(item, lastBooking, nextBooking,
                        comments.stream().map(ItemConverter::toCommentDto).collect(Collectors.toList()));
                itemInfoDtos.add(itemInfoDto);
            }
        }
        log.info("Находим все веши пользователя: {} {}", userId, itemInfoDtos);
        return itemInfoDtos.stream()
                .sorted(this::compare)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getSearchItem(String text) {
        if (text.isBlank()) {
            List<Item> blankItems = new ArrayList<>();
            return blankItems.stream()
                    .map(ItemConverter::toItemDto)
                    .collect(Collectors.toList());
        }
        List<Item> items = itemRepository.findByDescriptionContainingIgnoreCaseAndAvailableTrue(text);
        log.info("Находим все веши содержащие текст:  {} {}", text, items);
        return items.stream()
                .map(ItemConverter::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, long userId, long itemId) {
        Item item = itemRepository.getReferenceById(itemId);
        Booking booking = bookingRepository.findTopByItem(item);
        User author = userRepository.getReferenceById(userId);
        LocalDateTime now = LocalDateTime.now();
        if (booking.getBooker().getId().equals(userId) && booking.getEnd().isBefore(now)) {
            Comment comment = new Comment();
            comment.setCreated(now);
            item.getComments().add(comment);
            comment.setItem(item);
            comment.setAuthor(author);
            comment.setText(commentDto.getText());
            commentRepository.save(comment);
            log.info("Сохраняем новый комментарий: {}", comment);
            return ItemConverter.toCommentDto(comment);
        }
        log.error("Ошибка, валидация не пройдена. Пользователя с данным id не осуществлял бронирование: {}", userId);
        throw new ValidationException("Ошибка, валидация не пройдена. Пользователя с данным id " +
                "не осуществлял бронирование");
    }

    private void validate(Long userId, long id) {
        if (userId == null) {
            log.error("Ошибка, валидация не пройдена. Отсутствует id пользователя в запросе: {}", userId);
            throw new NotFoundException("Ошибка, валидация не пройдена. Отсутствует id пользователя в запросе");
        }
        if (!itemRepository.getReferenceById(id).getOwner().getId().equals(userId)) {
            log.error("Ошибка, валидация не пройдена. Пользователю с данным id вещь не принадлежит: {}", userId);
            throw new NotFoundException("Ошибка, валидация не пройдена. Пользователю с данным id " +
                    "вещь не принадлежит");
        }
    }

    private int compare(ItemInfoDto item1, ItemInfoDto item2) {
        return item1.getId().compareTo(item2.getId());
    }
}
