package ru.practicum.privatePart.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.UserDto;
import ru.practicum.privatePart.SubscriptionService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/subscription")
@AllArgsConstructor
public class SubscriptionController {

    private SubscriptionService subscriptionService;

    @PostMapping(value = "/{subscriberId}/user/{initiatorId}")
    public ResponseEntity<Object> addSubscription(@PathVariable("subscriberId") Long subscriberId,
                                                  @PathVariable("initiatorId") Long initiatorId) {
        log.debug("Запрос на добавление новой подписки пользователя {} на пользователя {}", subscriberId, initiatorId);
        return new ResponseEntity<>(subscriptionService.addSubscription(subscriberId, initiatorId), HttpStatus.CREATED);
    }

    @GetMapping(value = "/events/{initiatorId}")
    public List<EventShortDto> getEvents(@PathVariable("initiatorId") Long initiatorId,
                                         @RequestParam(value = "from", defaultValue = "0") Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.debug("Запрос на получение актуальных событий пользователя {}", initiatorId);
        return subscriptionService.getEventsByInitiatorId(initiatorId, from, size);
    }

    @GetMapping(value = "/{subscriberId}")
    public List<UserDto> getStartersBySubscriber(@PathVariable("subscriberId") Long subscriberId,
                                                 @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.debug("Запрос на получение всех пользователей, на которых подписан пользователь  {}", subscriberId);
        return subscriptionService.getStartersBySubscriber(subscriberId, from, size);
    }

    @GetMapping(value = "/starter/{initiatorId}")
    public List<UserDto> getSubscribersByStarter(@PathVariable("initiatorId") Long initiatorId,
                                                 @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.debug("Запрос на получение всех подписчиков, которые подписаны на пользователя  {}", initiatorId);
        return subscriptionService.getSubscribersByStarter(initiatorId, from, size);
    }

    @GetMapping(value = "/{subscriberId}/events")
    public List<EventShortDto> getAllSubscriptionEvents(@PathVariable("subscriberId") Long subscriberId,
                                                        @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                        @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.debug("Запрос на получение актуальных событий от пользователя {}", subscriberId);
        return subscriptionService.getAllSubscriptionEvents(subscriberId, from, size);
    }

    @DeleteMapping(value = "/{subscriberId}/events/{initiatorId}")
    public void deleteSubscription(@PathVariable("subscriberId") Long subscriberId,
                                   @PathVariable("initiatorId") Long initiatorId) {
        log.debug("Запрос на удаление пользователем {} на пользователя  {}", subscriberId, initiatorId);
        subscriptionService.deleteSubscription(subscriberId, initiatorId);
    }
}

