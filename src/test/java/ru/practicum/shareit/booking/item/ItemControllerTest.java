package ru.practicum.shareit.booking.item;

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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    private ItemDto itemDto;

    private ItemDto itemUpdateDto;

    private ItemInfoDto itemInfoDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto(1L, "item1", "description1", true, 1L);
        itemInfoDto = new ItemInfoDto(1L, "item", "description", true, null, null, null);
        itemUpdateDto = new ItemDto(1L, "itemUpdate", "itemUpdate", true, 1L);
    }

    @Test
    void create() throws Exception {
        when(itemService.create(any(), anyLong()))
                .thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

        verify(itemService, times(1)).create(itemDto, 1L);
    }

    @Test
    void update() throws Exception {
        when(itemService.update(any(), anyLong(), anyLong()))
                .thenReturn(itemUpdateDto);

        mockMvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemUpdateDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemUpdateDto.getName())))
                .andExpect(jsonPath("$.description", is(itemUpdateDto.getDescription())));

        verify(itemService, times(1)).update(itemDto, 1L, 1L);
    }

    @Test
    void getItem() throws Exception {
        when(itemService.getItem(anyLong(), anyLong()))
                .thenReturn(itemInfoDto);

        mockMvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemInfoDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .param("id", "1")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemInfoDto.getName())))
                .andExpect(jsonPath("$.description", is(itemInfoDto.getDescription())));

        verify(itemService, times(1)).getItem(1L, 1L);
    }

    @Test
    void getItems() throws Exception {
        when(itemService.getItems(anyLong()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));

        verify(itemService, times(1)).getItems(anyLong());
    }

    @Test
    void getSearchItem() throws Exception {
        when(itemService.getSearchItem(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/items/search")
                        .param("text", ""))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("[]"));

        verify(itemService, times(1)).getSearchItem(anyString());
    }

}