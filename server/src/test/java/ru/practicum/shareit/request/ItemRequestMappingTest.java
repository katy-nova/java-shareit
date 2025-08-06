package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class ItemRequestMappingTest {

    @Autowired
    private ItemRequestMapping itemRequestMapping;

    @Test
    void fromDto_shouldMapItemRequestCreateDtoToItemRequest() {
        // Подготовка
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need a drill");

        // Выполнение
        ItemRequest result = itemRequestMapping.fromDto(dto);

        // Проверка
        assertNotNull(result);
        assertEquals("Need a drill", result.getDescription());
    }

    @Test
    void toDto_shouldMapItemRequestToItemRequestDto() {
        // Подготовка
        User requester = new User();
        requester.setId(1L);
        requester.setName("Requester");

        ItemRequest request = new ItemRequest();
        request.setId(1L);
        request.setDescription("Need item");
        request.setRequester(requester);
        request.setCreated(Instant.now());

        // Выполнение
        ItemRequestDto result = itemRequestMapping.toDto(request);

        // Проверка
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Need item", result.getDescription());
    }
}