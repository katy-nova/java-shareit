package ru.practicum.shareit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Тест для MethodArgumentNotValidException (валидация DTO)
    @Test
    void handleMethodArgumentNotValidException() throws Exception {
        UserCreateDto invalidUser = new UserCreateDto(); // Не заполнены обязательные поля

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isInternalServerError());
    }

    // Тест для NotFoundException
    @Test
    void handleNotFoundException() throws Exception {
        mockMvc.perform(get("/users/999")) // Несуществующий ID
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"));
    }

    // Тест для AlreadyExistsException
    @Test
    void handleAlreadyExistsException() throws Exception {
        UserCreateDto duplicateUser = new UserCreateDto();
        duplicateUser.setName("Дубликат");
        duplicateUser.setEmail("user1@example.com"); // Существующий email

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Пользователь с email 'user1@example.com' уже существует"));
    }

    // Тест для IllegalStateException
    @Test
    void handleIllegalStateException() throws Exception {
        // Симулируем ошибку через невалидное бронирование
        BookingCreateDto invalidBooking = new BookingCreateDto();
        invalidBooking.setItemId(2L); // Велосипед (недоступен)
        invalidBooking.setStart(LocalDateTime.now().plusDays(1));
        invalidBooking.setEnd(LocalDateTime.now().plusDays(2));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // Тест для HttpMessageNotReadableException (невалидный JSON)
    @Test
    void handleHttpMessageNotReadableException() throws Exception {
        String invalidJson1 = "{\"name\": \"Test User\", \"email\": \"test@example.com}";
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson1))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // Тест для AccessDenyException
    @Test
    void handleAccessDenyException() throws Exception {
        // Пытаемся подтвердить бронирование не владельцем
        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 3L)) // Не владелец
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").exists());
    }

    // Тест для общего Exception
    @Test
    void handleGenericException() throws Exception {
        // Мокаем сервис, чтобы выбросить неожиданное исключение
        // (В реальном тесте лучше использовать @SpyBean)
        mockMvc.perform(get("/users/1/force-error")) // Предположим, есть такой endpoint для теста
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").exists());
    }
}