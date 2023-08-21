package ru.practicum.searchingService;

import ru.practicum.model.Event;
import ru.practicum.model.State;

import java.util.Collection;
import java.util.List;

public interface SearchingEventsByParameters {

    List<Event> getAdminEventsByParameters(Collection<Long> users,
                                           Collection<State> states,
                                           Collection<Integer> categories,
                                           String rangeStart,
                                           String rangeEnd,
                                           Integer from,
                                           Integer size);

    List<Event> getAllEventsByParameters(String text,
                                         Collection<Integer> categories,
                                         Boolean paid,
                                         String rangeStart,
                                         String rangeEnd,
                                         Boolean onlyAvailable,
                                         String sort,
                                         Integer from,
                                         Integer size);

}
