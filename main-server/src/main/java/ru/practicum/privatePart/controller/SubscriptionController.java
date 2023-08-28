package ru.practicum.privatePart.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.NewEventDto;
import ru.practicum.privatePart.SubscriptionService;

import javax.validation.Valid;

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
}
