package ru.practicum.mapper;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EventMapper {

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static Event getEvent(NewEventDto newEventDto, User initiator, Category category) {
        Event event = new Event();
        event.setEventDate(LocalDateTime.parse(newEventDto.getEventDate(), dateTimeFormatter));
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setCategory(category);
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setLocation(newEventDto.getLocation());
        event.setInitiator(initiator);
        event.setState(newEventDto.getState());
        event.setTitle(newEventDto.getTitle());
        event.setConfirmedRequests(newEventDto.getConfirmedRequests());
        event.setParticipationAvailable(newEventDto.getAvailable());
        event.setViews(newEventDto.getView());
        return event;
    }

    public static EventFullDto getEventFullDto(Event event) {
        EventFullDto eventFullDto = EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.getCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate((event.getEventDate()).format(dateTimeFormatter))
                .initiator(UserMapper.getUserShortDto(event.getInitiator()))
                .location(event.getLocation())
                .paid(event.getPaid())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .participantLimit(event.getParticipantLimit())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
        return eventFullDto;
    }

    public static EventShortDto getEventShortDto(Event event) {
        EventShortDto eventShortDto = EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.getCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(UserMapper.getUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .build();
        return eventShortDto;
    }
}
