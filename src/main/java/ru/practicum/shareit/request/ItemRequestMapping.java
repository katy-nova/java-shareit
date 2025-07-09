package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper(componentModel = "spring")
public interface ItemRequestMapping {

    public ItemRequest fromDto(ItemRequestCreateDto itemRequestCreateDto);

    public ItemRequestDto toDto(ItemRequest itemRequest);
}
