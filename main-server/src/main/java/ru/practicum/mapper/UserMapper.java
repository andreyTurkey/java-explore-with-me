package ru.practicum.mapper;

import ru.practicum.dto.UserDto;
import ru.practicum.dto.UserShortDto;
import ru.practicum.model.NewUserRequest;
import ru.practicum.model.User;

public class UserMapper {

    public static UserDto getUserDto(User user) {
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .subscriptionAvailability(user.getSubscriptionAvailability())
                .build();
        return userDto;
    }

    public static User getUser(NewUserRequest newUserRequest) {
        User user = User.builder()
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .subscriptionAvailability(newUserRequest.getSubscription())
                .build();
        return user;
    }

    public static UserShortDto getUserShortDto(User user) {
        UserShortDto userShortDto = UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
        return userShortDto;
    }
}
