package ru.practicum.shareit.requests;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {

    ItemRequestDto create(ItemRequestDto itemRequestDto, long userId);

    List<ItemRequestDto> getRequests(long userId);

    List<ItemRequestDto> getAllRequests(PageRequest pageRequest, long userId);

    ItemRequestDto getRequest(Long requestId, long userId);
}
