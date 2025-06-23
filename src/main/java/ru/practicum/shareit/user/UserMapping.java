package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Mapper(componentModel = "spring")
public interface UserMapping {

    public User fromDto(UserCreateDto userDto);

    public User fromUpdateDto(UserUpdateDto userUpdateDto);

    public UserDto toDto(User user);
}
