package ru.practicum.shareit.user.dto;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class UserUpdateDto {

    @Nullable
    private String name;

    @Nullable
    private String email;
}
