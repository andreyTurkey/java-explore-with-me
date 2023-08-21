package ru.practicum.admin;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.*;
import ru.practicum.exception.DuplicationException;
import ru.practicum.exception.NotAvailableException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.*;
import ru.practicum.repository.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminService {

    UserRepository userRepository;

    CategoryRepository categoryRepository;

    EventRepository eventRepository;

    LocationRepository locationRepository;

    CompilationRepository compilationRepository;

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        try {
            userRepository.save(UserMapper.getUser(newUserRequest));
        } catch (Exception ex) {
            throw new DuplicationException("Имя пользователя существует");
        }
        return UserMapper.getUserDto(userRepository.findByEmail(newUserRequest.getEmail()));
    }

    @Transactional
    public Category addCategory(NewCategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new DuplicationException("Категория существует");
        }
        categoryRepository.save(CategoryMapper.getCategoryFromNew(categoryDto));
        return categoryRepository.findByName(categoryDto.getName());
    }

    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getPinned() == null) {
            newCompilationDto.setPinned(false);
        }
        compilationRepository.save(CompilationMapper.getCompilation(newCompilationDto));
        List<EventShortDto> eventShortDtos;
        if (newCompilationDto.getEvents() == null) {
            eventShortDtos = new ArrayList<>();
        } else {
            eventShortDtos = eventRepository.findAllByIdIn(newCompilationDto.getEvents())
                    .stream()
                    .map(EventMapper::getEventShortDto)
                    .collect(Collectors.toList());
        }
        return CompilationMapper.getCompilationDto(compilationRepository.findAllByTitle(newCompilationDto.getTitle()), eventShortDtos);
    }

    @Transactional
    public CategoryDto changeCategory(Integer catId, CategoryDto categoryDto) {
        if (categoryDto.getName() != null) {
            if (categoryRepository.existsByName(categoryDto.getName())) {
                Category categoryForCheckName = categoryRepository.findByName(categoryDto.getName());
                if (!categoryForCheckName.getId().equals(catId)) {
                    throw new DuplicationException("Категория существует");
                }
            } else {
                Category oldCategory = categoryRepository.getReferenceById(catId);
                oldCategory.setName(categoryDto.getName());
                categoryRepository.save(oldCategory);
            }
        } else {
            Category oldCategory = categoryRepository.getReferenceById(catId);
            oldCategory.setName(categoryDto.getName());
            categoryRepository.save(oldCategory);
        }
        return CategoryMapper.getCategoryDto(categoryRepository.getReferenceById(catId));
    }

    @Transactional
    public CompilationDto changeCompilation(Long compId, UpdateCompilationRequest ucr) {
        Compilation oldCompilation = compilationRepository.getReferenceById(compId);
        List<EventShortDto> eventShortDtos;
        if (ucr.getEvents() != null) {
            oldCompilation.setEvents(ucr.getEvents());
            eventShortDtos = eventRepository.findAllByIdIn(ucr.getEvents())
                    .stream()
                    .map(EventMapper::getEventShortDto)
                    .collect(Collectors.toList());
        } else {
            eventShortDtos = eventRepository.findAllByIdIn(oldCompilation.getEvents())
                    .stream()
                    .map(EventMapper::getEventShortDto)
                    .collect(Collectors.toList());
        }
        if (ucr.getPinned() != null) {
            oldCompilation.setPinned(ucr.getPinned());
        }
        if (ucr.getTitle() != null) {
            oldCompilation.setTitle(ucr.getTitle());
        }
        compilationRepository.save(oldCompilation);
        return CompilationMapper.getCompilationDto(oldCompilation, eventShortDtos);
    }

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

    public List<EventFullDto> getEventsByParameter(
            List<Long> users,
            List<State> states,
            List<Integer> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size) {

        return eventRepository.getAdminEventsByParameters(
                        users,
                        states,
                        categories,
                        rangeStart,
                        rangeEnd,
                        from,
                        size)
                .stream()
                .map(EventMapper::getEventFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto updateEvent(UpdateEventAdminRequest body, Long eventId) {
        Event oldEvent = eventRepository.getReferenceById(eventId);

        if (body.getEventDate() != null) {
            if (LocalDateTime.parse(body.getEventDate(), dateTimeFormatter).isBefore(LocalDateTime.now().plusHours(1))) {
                throw new NotAvailableException("Дата и время события указаны менее, чем за 2 часа до текущего момента");
            }
        }
        if (body.getPaid() != null) {
            oldEvent.setPaid(body.getPaid());
        }
        if (body.getCategoryId() != null) {
            Category category = categoryRepository.getReferenceById(body.getCategoryId());
            oldEvent.setCategory(category);
        }
        if (body.getEventDate() != null) {
            oldEvent.setEventDate(LocalDateTime.parse(body.getEventDate(), dateTimeFormatter));
        }
        if (body.getDescription() != null) {
            oldEvent.setDescription(body.getDescription());
        }
        if (body.getLocation() != null) {
            locationRepository.save(body.getLocation());
            oldEvent.setLocation(body.getLocation());
        }
        if (body.getAnnotation() != null) {
            oldEvent.setAnnotation(body.getAnnotation());
        }
        if (body.getTitle() != null) {
            oldEvent.setTitle(body.getTitle());
        }
        if (body.getStateAction() != null) {
            if (body.getStateAction().equals(State.REJECT_EVENT)) {
                if (oldEvent.getState().equals(State.PUBLISHED)) {
                    throw new DuplicationException("Событие уже опубликовано");
                } else {
                    oldEvent.setState(State.REJECTED);
                }
            }
            if (body.getStateAction().equals(State.PUBLISH_EVENT))
                if (oldEvent.getState().equals(State.PUBLISHED)) {
                    throw new DuplicationException("Событие уже опубликовано");
                } else if (oldEvent.getState().equals(State.REJECTED)) {
                    throw new DuplicationException("Событие уже отменено");
                } else {
                    oldEvent.setState(State.PUBLISHED);
                }
        }
        if (body.getParticipantLimit() != null) {
            oldEvent.setParticipantLimit(body.getParticipantLimit());
        }
        if (body.getRequestModeration() == null && !oldEvent.getRequestModeration()) {
            oldEvent.setState(State.PUBLISHED);
        }
        eventRepository.save(oldEvent);
        return EventMapper.getEventFullDto(oldEvent);
    }

    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                "Пользователь ID = " + userId + " не найден."));
        userRepository.deleteById(userId);
    }

    @Transactional
    public void deleteCategory(Integer catId) {
        categoryRepository.findById(catId).orElseThrow(() -> new EntityNotFoundException(
                "Категория ID = " + catId + " не найдена."));
        if (eventRepository.existsByCategoryId(catId)) {
            throw new DuplicationException("Нельзя удалить категорию. Есть связанные события.");
        }
        categoryRepository.deleteById(catId);
    }

    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() -> new EntityNotFoundException(
                "Подборка ID = " + compId + " не найдена."));
        compilationRepository.deleteById(compId);
    }
}
