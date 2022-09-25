package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDto create(UserDto userDto) {
        User user = UserConverter.toUser(userDto);
        log.info("Сохраняем нового пользователя: {}", user);
        return UserConverter.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto getUser(long id) {
        if (userRepository.findById(id).isEmpty()) {
            log.error("Ошибка, валидация не пройдена. Пользователя с данным id не существует: {}", id);
            throw new NotFoundException("Ошибка, валидация не пройдена. Пользователя с данным id " +
                    "не существует");
        } else {
            log.info("Находим пользователя с id: {} {}", id, userRepository.getReferenceById(id));
            return UserConverter.toUserDto(userRepository.getReferenceById(id));
        }
    }

    @Override
    public List<UserDto> getUsers() {
        Collection<User> users = userRepository.findAll();
        log.info("Находим всех существующих пользователей: {}", users);
        return users.stream()
                .map(UserConverter::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(UserDto userDto, long id) {
        User user = userRepository.getReferenceById(id);
        String name = userDto.getName();
        if (name != null) {
            user.setName(name);
        }
        String email = userDto.getEmail();
        if (email != null) {
            user.setEmail(email);
        }
        log.info("Обновляем данные пользователя: {}", user);
        return UserConverter.toUserDto(userRepository.save(user));
    }

    @Override
    public void delete(long id) {
        log.info("Удаляем пользователя c id: {}", id);
        userRepository.deleteById(id);
    }
}
