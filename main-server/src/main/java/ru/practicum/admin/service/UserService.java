package ru.practicum.admin.service;

import ru.practicum.dto.UpdateUserDto;
import ru.practicum.dto.UserDto;
import ru.practicum.model.NewUserRequest;
import ru.practicum.model.User;

import java.util.List;

public interface UserService {

    UserDto addUser(NewUserRequest newUserRequest);

    List<User> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);

    UserDto changeUser(UpdateUserDto userDto, Long userId);
}
