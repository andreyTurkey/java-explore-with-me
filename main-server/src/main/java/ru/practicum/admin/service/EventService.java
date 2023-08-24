package ru.practicum.admin.service;

import ru.practicum.dto.EventFullDto;
import ru.practicum.model.State;
import ru.practicum.model.UpdateEventAdminRequest;

import java.util.List;

public interface EventService {

    List<EventFullDto> getEventsByParameter(
            List<Long> users,
            List<State> states,
            List<Integer> categories,
            String rangeStart,
            String rangeEnd,
            Integer from,
            Integer size);

    EventFullDto updateEvent(UpdateEventAdminRequest body, Long eventId);
}
