package ru.practicum.shareit.item.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemCreateDto {
    private String name;
    private String description;
    private Boolean available;

    @Nullable
    private Long requestId;
}
