package ru.practicum.admin.service;

import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.AdminParametersDto;
import ru.practicum.model.UpdateEventAdminRequest;

import java.util.List;

public interface EventService {

    List<EventFullDto> getEventsByParameter(AdminParametersDto adminParametersDto);

    EventFullDto updateEvent(UpdateEventAdminRequest body, Long eventId);
}
