package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentCreateDto {
    // вот нахрена? почему нельзя в контроллере сделать просто @RequestBody String text????
    @NotNull
    @NotEmpty
    private String text;
}
