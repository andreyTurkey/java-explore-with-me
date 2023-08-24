package ru.practicum.admin.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.service.UserService;
import ru.practicum.dto.UserDto;
import ru.practicum.model.NewUserRequest;
import ru.practicum.model.User;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody NewUserRequest body) {
        log.debug("Запрос на добавление нового пользователя {}", body);
        return new ResponseEntity<>(userService.addUser(body), HttpStatus.CREATED);
    }

    @GetMapping
    public List<User> getUsersByIds(@RequestParam(value = "ids", required = false) List<Long> ids,
                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.debug("Запрошены пользователи ID = {}", ids);
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping(value = "{userId}")
    public ResponseEntity<Objects> deleteUser(@Positive @PathVariable("userId") Long userId) {
        log.debug("Запрошено удаление пользователя с ID = {}", userId);
        userService.deleteUser(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
