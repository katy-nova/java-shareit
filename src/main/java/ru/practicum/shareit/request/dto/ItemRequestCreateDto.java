package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequestCreateDto {

    @NotNull
    @Size(max = 200)
    private String description;

}
