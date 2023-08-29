package ru.practicum.admin.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.UpdateUserDto;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.DuplicationException;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.NewUserRequest;
import ru.practicum.model.User;
import ru.practicum.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByName(newUserRequest.getName())) {
            throw new DuplicationException("Имя уже существует.");
        }
        if (newUserRequest.getSubscription() == null) {
            newUserRequest.setSubscription(true);
        }
        userRepository.save(UserMapper.getUser(newUserRequest));
        return UserMapper.getUserDto(userRepository.findByEmail(newUserRequest.getEmail()));
    }

    @Override
    @Transactional
    public UserDto changeUser(UpdateUserDto updateUserDto, Long userId) {
        User user = userRepository.getReferenceById(userId);
        if (updateUserDto.getSubscription() != null) {
            user.setSubscription(updateUserDto.getSubscription());
        }
        if (updateUserDto.getEmail() != null) {
            user.setEmail(updateUserDto.getEmail());
        }
        if (updateUserDto.getName() != null) {
            user.setName(updateUserDto.getName());
        }
        return UserMapper.getUserDto(user);
    }

    @Override
    public List<User> getUsers(List<Long> ids, Integer from, Integer size) {
        Sort sortById = Sort.by(Sort.Direction.ASC, "id");
        if (ids != null && from != null && size != null) {
            Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, sortById);
            return userRepository.findAllByIdIn(ids, page).stream().collect(Collectors.toList());
        } else if (ids != null && from == null) {
            return userRepository.findAllByIdIn(ids);
        } else if (ids == null && from != null && size != null) {
            Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, sortById);
            return userRepository.findAllBy(page);
        } else if (ids == null && from == null) {
            return userRepository.findAllBy();
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                "Пользователь ID = " + userId + " не найден."));
        userRepository.deleteById(userId);
    }
}
