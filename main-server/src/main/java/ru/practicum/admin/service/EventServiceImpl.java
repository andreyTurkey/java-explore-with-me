package ru.practicum.admin.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventFullDto;
import ru.practicum.exception.DuplicationException;
import ru.practicum.exception.NotAvailableException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.State;
import ru.practicum.model.UpdateEventAdminRequest;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventServiceImpl implements EventService {

    CategoryRepository categoryRepository;

    EventRepository eventRepository;

    LocationRepository locationRepository;

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
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

    @Override
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
}
