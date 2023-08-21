package ru.practicum.privatePart.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.EventRequestStatusUpdateRequest;
import ru.practicum.model.UpdateEventUserRequest;
import ru.practicum.privatePart.PrivateEventService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class PrivateEventController {

    private final PrivateEventService privateEventService;

    @PostMapping(value = "/{userId}/events")
    public ResponseEntity<Object> addEvent(@Valid @RequestBody NewEventDto body,
                                           @PathVariable("userId") Long userId) {
        log.debug("Запрос на добавление нового события {}", userId);
        return new ResponseEntity<>(privateEventService.addEvent(body, userId), HttpStatus.CREATED);
    }

    @PostMapping(value = "/{userId}/requests")
    public ResponseEntity<Object> addRequests(@PathVariable("userId") Long userId,
                                              @RequestParam(value = "eventId") Long eventId) {
        log.debug("Запрос на добавление заявки на участие в событии ID = {}, от пользователя {}", eventId, userId);
        return new ResponseEntity<>(privateEventService.addRequest(userId, eventId), HttpStatus.CREATED);
    }

    @GetMapping(value = "/{userId}/events")
    public List<EventShortDto> getEventByIdByUserId(@PathVariable(value = "userId", required = false) Long userId,
                                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.debug("Запрос  событий пользователя ID = {}", userId);
        return privateEventService.getEventsByUserId(userId, from, size);
    }

    @GetMapping(value = "/{userId}/events/{eventId}")
    public EventFullDto getEventByIdByUserId(@PathVariable("userId") Long userId,
                                             @PathVariable("eventId") Long eventId) {
        log.debug("Запрос  событий пользователя ID = {}, события ID = {}", userId, eventId);
        return privateEventService.getEventsByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping(value = "/{userId}/events/{eventId}")
    public EventFullDto changeEventByIdByUserId(@Valid @RequestBody UpdateEventUserRequest body,
                                                @PathVariable("userId") Long userId,
                                                @PathVariable("eventId") Long eventId) {
        log.debug("Запрос на изменение событий пользователя ID = {}, события ID = {}", userId, eventId);
        return privateEventService.changeEventsByUserIdAndEventId(userId, eventId, body);
    }

    @PatchMapping(value = "/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable("userId") Long userId,
                                                 @PathVariable("requestId") Long requestId) {
        log.debug("ЗАПРОС НА ОТМЕНУ ЗАЯВКИ НА УЧАСТИЕ ОТ ПОЛЬЗОВАТЕЛЯ id = {} И ЗАЯВКИ id = {}", userId, requestId);
        return privateEventService.cancelRequest(userId, requestId);
    }

    @PatchMapping(value = "/{userId}/events/{eventId}/requests")
    public Map<String, List<ParticipationRequestDto>> changeRequestStatus(@Valid @RequestBody EventRequestStatusUpdateRequest body,
                                                                          @PathVariable("userId") Long userId,
                                                                          @PathVariable("eventId") Long eventId) {
        log.debug("ЗАПРОС НА ИЗМЕНЕНИЕ СТАТУСА ЗАЯВКИ НА УЧАСТИЕ ОТ ПОЛЬЗОВАТЕЛЯ id = {} И СОБЫТИЯ id = {}", userId, eventId);
        return privateEventService.changeRequestStatus(body, userId, eventId);
    }

    @GetMapping(value = "/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsEventsOtherUsers(@PathVariable("userId") Long userId) {
        log.debug("Запрос заявок на участие в событиях пользователя ID = {}", userId);
        return privateEventService.getRequestsByUserId(userId);
    }

    @GetMapping(value = "/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsOwnEvents(@PathVariable("userId") Long userId,
                                                              @PathVariable("eventId") Long eventId) {
        log.debug("Запрос заявок на участие в событии пользователя ID = {} и события ID = {}", userId, eventId);
        return privateEventService.getRequestsOwnEvents(userId, eventId);
    }
}
