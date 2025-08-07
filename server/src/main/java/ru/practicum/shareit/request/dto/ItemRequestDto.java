package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoForRequest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
public class ItemRequestDto {

    private Long id;
    private String description;
    private Instant created;
    private List<ItemDtoForRequest> items = new ArrayList<>();
}
