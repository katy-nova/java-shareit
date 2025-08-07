package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoSimple;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class BookingMappingTest {

    @Autowired
    private BookingMapping bookingMapping;

    @MockBean
    private ItemRepository itemRepository;

    @Test
    void fromDto_shouldMapBookingCreateDtoToBooking() {
        // Подготовка тестовых данных
        Item testItem = new Item();
        testItem.setId(1L);
        testItem.setName("Test Drill");

        // Настраиваем мок репозитория
        when(itemRepository.findById(1L)).thenReturn(Optional.of(testItem));
        when(itemRepository.findById(999L)).thenReturn(Optional.empty());

        // Тест 1: Успешный маппинг с существующим item
        BookingCreateDto validDto = new BookingCreateDto();
        validDto.setItemId(1L);
        validDto.setStart(LocalDateTime.now().plusDays(1));
        validDto.setEnd(LocalDateTime.now().plusDays(2));

        Booking result = bookingMapping.fromDto(validDto);

        assertNotNull(result);
        assertEquals(validDto.getStart(), result.getStart());
        assertEquals(validDto.getEnd(), result.getEnd());
        assertNotNull(result.getItem());
        assertEquals(1L, result.getItem().getId());

        // Тест 2: Попытка маппинга с несуществующим item
        BookingCreateDto invalidDto = new BookingCreateDto();
        invalidDto.setItemId(999L); // Несуществующий ID

        Booking nullItemResult = bookingMapping.fromDto(invalidDto);
        assertNotNull(nullItemResult);
        assertNull(nullItemResult.getItem());
    }

    @Test
    void toDto_shouldMapBookingToBookingDto() {
        // Подготовка
        User booker = new User();
        booker.setId(1L);
        booker.setName("Test Booker");

        Item item = new Item();
        item.setId(1L);
        item.setName("Test Item");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setBooker(booker);
        booking.setItem(item);

        // Выполнение
        BookingDto result = bookingMapping.toDto(booking);

        // Проверка
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1L, result.getBooker().getId());
        assertEquals(1L, result.getItem().getId());
    }

    @Test
    void toDtoSimple_shouldMapBookingToBookingDtoSimple() {
        // Подготовка
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now());

        // Выполнение
        BookingDtoSimple result = bookingMapping.toDtoSimple(booking);

        // Проверка
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}