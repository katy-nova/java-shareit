package ru.practicum.shareit.request;


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
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "/booking.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getRequestsByUserId_shouldReturnUserRequests() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель для ремонта квартиры"))
                .andExpect(jsonPath("$[0].items[0].name").value("Дрель"));
    }

    @Test
    void getRequestsByUserId_shouldReturnEmptyListWhenNoRequests() throws Exception {
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    void getAllRequestsBesidesUsers_shouldReturnOtherUsersRequests() throws Exception {
        mockMvc.perform(get("/requests" + "/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description").value("Нужна дрель для ремонта квартиры"));
    }

    @Test
    void getAllRequestsBesidesUsers_shouldReturnEmptyListWhenNoOtherRequests() throws Exception {
        mockMvc.perform(get("/requests" + "/all")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createRequest_shouldCreateNewRequest() throws Exception {
        ItemRequestCreateDto createDto = new ItemRequestCreateDto();
        createDto.setDescription("Нужен перфоратор");

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(createDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Нужен перфоратор"))
                .andExpect(jsonPath("$.items").isEmpty());
    }


    @Test
    void getRequest_shouldReturnRequestWithItems() throws Exception {
        mockMvc.perform(get("/requests" + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Нужна дрель для ремонта квартиры"))
                .andExpect(jsonPath("$.items[0].name").value("Дрель"));
    }

    @Test
    void getRequest_shouldReturnNotFoundWhenRequestNotExists() throws Exception {
        mockMvc.perform(get("/requests" + "/999")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }
}