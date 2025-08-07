package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql(scripts = "/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemRequestServiceTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    void create_shouldCreateNewRequest() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужен перфоратор");

        ItemRequestDto result = itemRequestService.create(createDto, 1L);

        assertNotNull(result.getId());
        assertEquals("Нужен перфоратор", result.getDescription());
        assertNotNull(result.getCreated());

        ItemRequest savedRequest = itemRequestRepository.findById(result.getId()).orElseThrow();
        assertEquals(1L, savedRequest.getRequester().getId());
        assertEquals("Нужен перфоратор", savedRequest.getDescription());
    }

    @Test
    void create_shouldThrowExceptionWhenUserNotFound() {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужен перфоратор");

        assertThrows(NotFoundException.class, () -> itemRequestService.create(createDto, 999L));
    }

    @Test
    void getItemsByUser_shouldReturnUserRequests() {
        List<ItemRequestDto> result = itemRequestService.getItemsByUser(2L, 0, 10);

        assertEquals(1, result.size());
        assertEquals("Нужна дрель для ремонта квартиры", result.get(0).getDescription());
        assertEquals(1, result.get(0).getItems().size());
        assertEquals("Дрель", result.get(0).getItems().get(0).getName());
    }

    @Test
    void getItemsByUser_shouldReturnEmptyListWhenNoRequests() {
        List<ItemRequestDto> result = itemRequestService.getItemsByUser(1L, 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void getItemsByUser_shouldThrowExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemsByUser(999L, 0, 10));
    }

    @Test
    void getAllBesidesUsers_shouldReturnOtherUsersRequests() {
        List<ItemRequestDto> result = itemRequestService.getAllBesidesUsers(1L, 0, 10);

        assertEquals(1, result.size());
        assertEquals("Нужна дрель для ремонта квартиры", result.get(0).getDescription());
    }

    @Test
    void getAllBesidesUsers_shouldReturnEmptyListWhenNoOtherRequests() {
        List<ItemRequestDto> result = itemRequestService.getAllBesidesUsers(2L, 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllBesidesUsers_shouldThrowExceptionWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllBesidesUsers(999L, 0, 10));
    }

    @Test
    void getItem_shouldReturnRequestWithItems() {
        ItemRequestDto result = itemRequestService.getItem(1L);

        assertEquals("Нужна дрель для ремонта квартиры", result.getDescription());
        assertEquals(1, result.getItems().size());
        assertEquals("Дрель", result.getItems().get(0).getName());
    }

    @Test
    void getItem_shouldThrowExceptionWhenRequestNotFound() {
        assertThrows(NotFoundException.class, () -> itemRequestService.getItem(999L));
    }

    @Test
    void getItemsByUser_shouldHandlePaginationCorrectly() {
        // Создаем несколько запросов для теста пагинации
        for (int i = 0; i < 5; i++) {
            ItemRequestCreateDto createDto = new ItemRequestCreateDto();
            createDto.setDescription("Request " + i);
            itemRequestService.create(createDto, 3L);
        }

        // Первая страница с 2 элементами
        List<ItemRequestDto> page1 = itemRequestService.getItemsByUser(3L, 0, 2);
        assertEquals(2, page1.size());

        // Вторая страница с 2 элементами
        List<ItemRequestDto> page2 = itemRequestService.getItemsByUser(3L, 2, 2);
        assertEquals(2, page2.size());

        // Третья страница с 1 элементом
        List<ItemRequestDto> page3 = itemRequestService.getItemsByUser(3L, 4, 2);
        assertEquals(1, page3.size());
    }
}