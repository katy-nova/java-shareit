package ru.practicum.shareit.item.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class ItemUpdateDto {

    @Nullable
    private String name;

    @Nullable
    private String description;

    @Nullable
    private Boolean available;

}
