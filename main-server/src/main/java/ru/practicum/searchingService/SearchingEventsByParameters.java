package ru.practicum.searchingService;

import ru.practicum.dto.AdminParametersDto;
import ru.practicum.dto.PublicParametersDto;
import ru.practicum.model.Event;

import java.util.List;

public interface SearchingEventsByParameters {

    List<Event> getAdminEventsByParameters(AdminParametersDto adminParametersDto);

    List<Event> getAllEventsByParameters(PublicParametersDto publicParametersDto);
}
