package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Sql(scripts = "/booking.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void getUser_shouldReturnUserWhenExists() {
        UserDto userDto = userService.getUser(1L);

        assertEquals("Иван Иванов", userDto.getName());
        assertEquals("user1@example.com", userDto.getEmail());
    }

    @Test
    void getUser_shouldThrowNotFoundExceptionWhenUserNotExists() {
        assertThrows(NotFoundException.class, () -> userService.getUser(999L));
    }

    @Test
    void createUser_shouldCreateNewUser() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("Новый пользователь");
        createDto.setEmail("new@example.com");

        UserDto result = userService.createUser(createDto);

        assertNotNull(result.getId());
        assertEquals("Новый пользователь", result.getName());
        assertEquals("new@example.com", result.getEmail());

        User savedUser = userRepository.findById(result.getId()).orElseThrow();
        assertEquals("Новый пользователь", savedUser.getName());
    }

    @Test
    void createUser_shouldThrowAlreadyExistsExceptionForDuplicateEmail() {
        UserCreateDto createDto = new UserCreateDto();
        createDto.setName("Дубликат");
        createDto.setEmail("user1@example.com"); // email уже существует

        assertThrows(AlreadyExistsException.class, () -> userService.createUser(createDto));
    }

    @Test
    void updateUser_shouldUpdateName() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Обновленное имя");

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("Обновленное имя", result.getName());
        assertEquals("user1@example.com", result.getEmail()); // email не изменился

        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals("Обновленное имя", updatedUser.getName());
    }

    @Test
    void updateUser_shouldUpdateEmailWhenNotDuplicate() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("new.email@example.com");

        UserDto result = userService.updateUser(1L, updateDto);

        assertEquals("new.email@example.com", result.getEmail());
        assertEquals("Иван Иванов", result.getName()); // имя не изменилось
    }

    @Test
    void updateUser_shouldThrowAlreadyExistsExceptionForDuplicateEmail() {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail("user2@example.com"); // email принадлежит другому пользователю

        assertThrows(AlreadyExistsException.class, () -> userService.updateUser(1L, updateDto));
    }

    @Test
    void updateUser_shouldNotChangeFieldsWhenNullValues() {
        UserDto beforeUpdate = userService.getUser(1L);

        UserUpdateDto updateDto = new UserUpdateDto(); // все поля null

        UserDto afterUpdate = userService.updateUser(1L, updateDto);

        assertEquals(beforeUpdate.getName(), afterUpdate.getName());
        assertEquals(beforeUpdate.getEmail(), afterUpdate.getEmail());
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        userService.deleteUser(1L);

        assertFalse(userRepository.existsById(1L));
    }

    @Test
    void deleteUser_shouldNotThrowExceptionWhenUserNotExists() {
        assertDoesNotThrow(() -> userService.deleteUser(999L));
    }

    @Test
    void updateUser_shouldUpdateOnlyNameWhenEmailNotProvided() {
        // Подготовка
        User originalUser = userRepository.findById(1L).orElseThrow();
        String originalEmail = originalUser.getEmail();

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Новое имя");
        updateDto.setEmail(null); // Явно null

        // Выполнение
        UserDto result = userService.updateUser(1L, updateDto);

        // Проверка
        assertEquals("Новое имя", result.getName());
        assertEquals(originalEmail, result.getEmail()); // Email не изменился

        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals("Новое имя", updatedUser.getName());
        assertEquals(originalEmail, updatedUser.getEmail());
    }

    @Test
    void updateUser_shouldUpdateOnlyEmailWhenNameNotProvided() {
        // Подготовка
        User originalUser = userRepository.findById(1L).orElseThrow();
        String originalName = originalUser.getName();
        String newEmail = "new.email@example.com";

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName(null); // Явно null
        updateDto.setEmail(newEmail);

        // Выполнение
        UserDto result = userService.updateUser(1L, updateDto);

        // Проверка
        assertEquals(originalName, result.getName()); // Имя не изменилось
        assertEquals(newEmail, result.getEmail());

        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals(originalName, updatedUser.getName());
        assertEquals(newEmail, updatedUser.getEmail());
    }

    @Test
    void updateUser_shouldNotUpdateEmailWhenSameEmailProvided() {
        // Подготовка
        User originalUser = userRepository.findById(1L).orElseThrow();
        String originalEmail = originalUser.getEmail();
        String originalName = originalUser.getName();

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setEmail(originalEmail); // Тот же email
        updateDto.setName("Новое имя");

        // Выполнение
        UserDto result = userService.updateUser(1L, updateDto);

        // Проверка
        assertEquals("Новое имя", result.getName());
        assertEquals(originalEmail, result.getEmail());

        // Дополнительная проверка, что checkEmail не вызывался
        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals("Новое имя", updatedUser.getName());
        assertEquals(originalEmail, updatedUser.getEmail());
    }

    @Test
    void updateUser_shouldNotUpdateNameWhenSameNameProvided() {
        // Подготовка
        User originalUser = userRepository.findById(1L).orElseThrow();
        String originalName = originalUser.getName();

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName(originalName); // То же имя
        updateDto.setEmail("new.email@example.com");

        // Выполнение
        UserDto result = userService.updateUser(1L, updateDto);

        // Проверка
        assertEquals(originalName, result.getName()); // Имя не изменилось
        assertEquals("new.email@example.com", result.getEmail());

        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals(originalName, updatedUser.getName());
        assertEquals("new.email@example.com", updatedUser.getEmail());
    }

    @Test
    void updateUser_shouldUpdateBothFieldsWhenBothChanged() {
        // Подготовка
        String newName = "Полностью новое имя";
        String newEmail = "completely.new@example.com";

        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName(newName);
        updateDto.setEmail(newEmail);

        // Выполнение
        UserDto result = userService.updateUser(1L, updateDto);

        // Проверка
        assertEquals(newName, result.getName());
        assertEquals(newEmail, result.getEmail());

        User updatedUser = userRepository.findById(1L).orElseThrow();
        assertEquals(newName, updatedUser.getName());
        assertEquals(newEmail, updatedUser.getEmail());
    }
}