package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    UserServiceImpl userService;

    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    User user1 = new User(1L, "User1", "user1@mail");
    UserDto userDto = new UserDto(1L, "UserDto", "userDto@mail");

    @Test
    void create() {

        User user = UserConverter.toUser(userDto);
        when(userRepository.save(any()))
                .thenReturn(user);

        UserDto newUserDto = userService.create(userDto);

        assertNotNull(newUserDto);
        assertEquals(1L, newUserDto.getId());
        assertEquals("UserDto", newUserDto.getName());
        assertEquals("userDto@mail", newUserDto.getEmail());
    }

    @Test
    void getUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(userRepository.getReferenceById(1L))
                .thenReturn(user1);

        final UserDto userDto = userService.getUser(1L);

        assertNotNull(userDto);
    }

    @Test
    void getUsers() {
    when(userRepository.findAll())
            .thenReturn(Collections.singletonList(user1));

   final List<UserDto> userDtos = userService.getUsers();

   assertNotNull(userDtos);
   assertEquals(1, userDtos.size());
    }

    @Test
    void update() {
        when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user1);
        User user = UserConverter.toUser(userDto);
        when(userRepository.save(any()))
                .thenReturn(user);
        final UserDto newUserDto = userService.update(userDto, 1L);
        assertNotNull(newUserDto);
        assertEquals(1, newUserDto.getId());
        assertEquals("UserDto", newUserDto.getName());
    }

    @Test
    void delete() {
        willDoNothing().given(userRepository).deleteById(anyLong());
        userService.delete(anyLong());
        verify(userRepository, times(1)).deleteById(anyLong());
    }
}