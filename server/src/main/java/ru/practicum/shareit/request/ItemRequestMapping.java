package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.ItemMapping;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper(componentModel = "spring", uses = ItemMapping.class)
public interface ItemRequestMapping {

    public ItemRequest fromDto(ItemRequestCreateDto itemRequestCreateDto);

    public ItemRequestDto toDto(ItemRequest itemRequest);
}
