package ru.practicum.publicPart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import ru.practicum.EwmClient;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.*;
import ru.practicum.exception.NotAvailableException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.model.State;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.searchingService.SearchingEventsByParameters;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@org.springframework.stereotype.Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Service {

    CompilationRepository compilationRepository;

    SearchingEventsByParameters searchingEventsByParameters;

    EventRepository eventRepository;

    CategoryRepository categoryRepository;

    EwmClient ewmClient;

    final String uriPrefix = "/events";

    final String nameService = "ewm_main_service";


    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<CompilationDto> getCompilationByParameters(Boolean pinned,
                                                           Integer from,
                                                           Integer size) {
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size);

        List<Compilation> compilations;
        List<Long> eventsIds = new ArrayList<>();

        if (pinned != null) {
            compilations = compilationRepository.findAllByPinned(pinned, page).getContent();
        } else {
            compilations = compilationRepository.findAll(page).getContent();
        }
        for (Compilation comp : compilations) {
            eventsIds.addAll(comp.getEvents());
        }

        List<EventShortDto> events = eventRepository.findAllByIdIn(eventsIds)
                .stream()
                .map(EventMapper::getEventShortDto)
                .collect(Collectors.toList());

        Map<Long, EventShortDto> eventMap = events
                .stream()
                .collect(Collectors.toMap(EventShortDto::getId, Function.identity()));

        Map<Compilation, List<Long>> compilationListMap = compilations.stream()
                .collect(Collectors.toMap(Function.identity(), Compilation::getEvents));

        List<CompilationDto> compilationDto = new ArrayList<>();

        for (Map.Entry<Compilation, List<Long>> pair : compilationListMap.entrySet()) {
            List<EventShortDto> newEvent = pair.getValue()
                    .stream()
                    .map(eventMap::get)
                    .collect(Collectors.toList());
            CompilationDto compilationDto1 = CompilationMapper.getCompilationDto(pair.getKey(), newEvent);
            compilationDto.add(compilationDto1);
        }
        return compilationDto;
    }

    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.getReferenceById(compId);
        List<EventShortDto> eventShortDtos = eventRepository.findAllByIdIn(compilation.getEvents())
                .stream()
                .map(EventMapper::getEventShortDto)
                .collect(Collectors.toList());
        return CompilationMapper.getCompilationDto(compilationRepository.getReferenceById(compId), eventShortDtos);
    }

    public List<EventFullDto> getEventsByParameters(PublicParametersDto publicParametersDto) {
        HitDto hitDto = HitDto.builder()
                .app(nameService)
                .ip(publicParametersDto.getRequest().getRequestURI())
                .uri(uriPrefix)
                .timestamp(LocalDateTime.now())
                .build();

        ewmClient.addHit(hitDto);

        if (publicParametersDto.getRangeEnd() != null) {
            if (LocalDateTime.parse(publicParametersDto.getRangeEnd(), dateTimeFormatter).isBefore(LocalDateTime.now().plusHours(2))) {
                throw new NotAvailableException("Даты и время указаны неверно");
            }
        }

        List<Event> events = new ArrayList<>(searchingEventsByParameters.getAllEventsByParameters(publicParametersDto));

        LocalDateTime endSearching = LocalDateTime.now();
        LocalDateTime startSearching = LocalDateTime.now().plusHours(1);

        for (Event event : events) {
            if (event.getEventDate().isAfter(endSearching)) {
                endSearching = event.getEventDate();
            }
        }
        for (Event event : events) {
            if (event.getEventDate().isBefore(endSearching)) {
                startSearching = event.getEventDate();
            }
        }

        Map<Long, Long> views = viewExtractor(endSearching, startSearching, events);

        List<EventFullDto> eventFullDtos = events.stream().map(EventMapper::getEventFullDto).collect(Collectors.toList());

        for (EventFullDto event : eventFullDtos) {
            if (views.containsKey(event.getId())) {
                event.setViews(views.get(event.getId()));
            }
        }
        return eventFullDtos;
    }

    private Map<Long, Long> viewExtractor(LocalDateTime endSearching,
                                          LocalDateTime startSearching,
                                          List<Event> events) {
        List<String> uris = events
                .stream()
                .map(Event::getId)
                .map(o -> {
                    String str = "/events/" + o;
                    return str;
                })
                .collect(Collectors.toList());

        ResponseEntity<List<ViewStatsDto>> viewsFromStat = ewmClient.getStats(
                uris,
                String.valueOf(startSearching),
                String.valueOf(endSearching),
                true);

        Map<Long, Long> viewsExtractor = new HashMap<>();

        List<ViewStatsDto> views = viewsFromStat.getBody();

        for (ViewStatsDto view : views) {
            String uri = view.getUri();
            String[] arrOfStr = uri.split("/", 3);
            if (arrOfStr.length == 3) {
                viewsExtractor.put(Long.valueOf(arrOfStr[2]), view.getHits());
            }
        }
        return viewsExtractor;
    }

    public EventFullDto getEventById(Long eventId, HttpServletRequest request) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotAvailableException("События не существует");
        }
        HitDto hitDto = HitDto.builder()
                .app(nameService)
                .ip(request.getRequestURI())
                .uri(uriPrefix + "/" + eventId)
                .timestamp(LocalDateTime.now())
                .build();

        ewmClient.addHit(hitDto);

        Event event = eventRepository.getReferenceById(eventId);

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EntityNotFoundException("Cобытие ID = " + eventId + " не найдено.");
        }

        LocalDateTime startSearching = LocalDateTime.now().minusDays(1);
        LocalDateTime endSearching = LocalDateTime.now().plusYears(100);
        List<Event> events = List.of(event);

        Map<Long, Long> views = viewExtractor(endSearching, startSearching, events);

        EventFullDto eventFullDto = EventMapper.getEventFullDto(event);

        List<Long> newKeys = new ArrayList<>(views.keySet());
        Long viewCount = newKeys.get(0);

        eventFullDto.setViews(views.get(viewCount));

        return eventFullDto;
    }

    public List<CategoryDto> getCategories(Integer from, Integer size) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, sortById);
        return categoryRepository.findAllBy(page)
                .stream()
                .map(CategoryMapper::getCategoryDto)
                .collect(Collectors.toList());
    }

    public CategoryDto getCategoriesById(Integer catId) {
        return CategoryMapper.getCategoryDto(categoryRepository.getReferenceById(catId));
    }
}
