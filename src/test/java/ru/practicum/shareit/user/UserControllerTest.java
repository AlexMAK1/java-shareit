package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {

    @MockBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private UserDto userDto;

    private UserDto updateUserDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "user",
                "user@email"
        );

        updateUserDto = new UserDto(
                1L,
                "updateUser",
                "updateUser@email"
        );
    }

    @Test
    void findAll() throws Exception {
        when(userService.getUsers())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));

        verify(userService, times(1)).getUsers();
    }

    @Test
    void getUser() throws Exception {
        when(userService.getUser(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void create() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        verify(userService, times(1)).create(userDto);
    }

    @Test
    void updateUser() throws Exception {
        when(userService.update(any(), anyLong()))
                .thenReturn(updateUserDto);
        mockMvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updateUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updateUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updateUserDto.getEmail())));

        verify(userService, times(1)).update(userDto, 1L);
    }

    @Test
    void deleteUser() throws Exception {
        long id = 1L;

        doNothing().when(userService).delete(id);
        mockMvc.perform(delete("/users/{id}", id))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
