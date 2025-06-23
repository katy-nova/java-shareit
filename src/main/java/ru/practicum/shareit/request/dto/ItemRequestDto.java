package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class ItemRequestDto {

    private String description;
    private Instant createdAt;
}
