package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserIdDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserMappingTest {

    @Autowired
    private UserMapping userMapping;

    @Test
    void fromDto_shouldMapUserCreateDtoToUser() {
        // Подготовка
        UserCreateDto dto = new UserCreateDto();
        dto.setName("Test User");
        dto.setEmail("test@example.com");

        // Выполнение
        User result = userMapping.fromDto(dto);

        // Проверка
        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void fromUpdateDto_shouldMapUserUpdateDtoToUser() {
        // Подготовка
        UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Updated Name");
        dto.setEmail("updated@example.com");

        // Выполнение
        User result = userMapping.fromUpdateDto(dto);

        // Проверка
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
    }

    @Test
    void toDto_shouldMapUserToUserDto() {
        // Подготовка
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        // Выполнение
        UserDto result = userMapping.toDto(user);

        // Проверка
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void toUserIdDto_shouldMapUserToUserIdDto() {
        // Подготовка
        User user = new User();
        user.setId(1L);

        // Выполнение
        UserIdDto result = userMapping.toUserIdDto(user);

        // Проверка
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}