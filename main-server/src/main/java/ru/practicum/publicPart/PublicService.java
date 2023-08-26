package ru.practicum.publicPart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.EwmClient;
import ru.practicum.HitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.CompilationDto;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
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
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PublicService {

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

        List<CompilationDto> compilationDtoList = new ArrayList<>();

        for (Compilation compilation : compilations) {
            List<Long> eventsId = compilation.getEvents();
            List<EventShortDto> eventsList = new ArrayList<>();
            for (EventShortDto event : events) {
                for (Long id : eventsId) {
                    if (id.equals(event.getId())) {
                        eventsList.add(event);
                    }
                }
            }
            compilationDtoList.add(CompilationMapper.getCompilationDto(compilation, eventsList));
        }
        return compilationDtoList;
    }

    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = compilationRepository.getReferenceById(compId);
        List<EventShortDto> eventShortDtos = eventRepository.findAllByIdIn(compilation.getEvents())
                .stream()
                .map(EventMapper::getEventShortDto)
                .collect(Collectors.toList());
        return CompilationMapper.getCompilationDto(compilationRepository.getReferenceById(compId), eventShortDtos);
    }

    public List<EventFullDto> getEventsByParameters(String text,
                                                    Collection<Integer> categories,
                                                    Boolean paid,
                                                    String rangeStart,
                                                    String rangeEnd,
                                                    Boolean onlyAvailable,
                                                    String sort,
                                                    Integer from,
                                                    Integer size,
                                                    HttpServletRequest request) {
        HitDto hitDto = HitDto.builder()
                .app(nameService)
                .ip(request.getRequestURI())
                .uri(uriPrefix)
                .timestamp(LocalDateTime.now())
                .build();

        ewmClient.addHit(hitDto);

        if (rangeEnd != null) {
            if (LocalDateTime.parse(rangeEnd, dateTimeFormatter).isBefore(LocalDateTime.now().plusHours(2))) {
                throw new NotAvailableException("Даты и время указаны неверно");
            }
        }

        List<Event> events = searchingEventsByParameters.getAllEventsByParameters(text,
                        categories,
                        paid,
                        rangeStart,
                        rangeEnd,
                        onlyAvailable,
                        sort,
                        from,
                        size)
                .stream()
                .collect(Collectors.toList());

        Map<Long, Long> views = viewExtractor(events, null);

        List<EventFullDto> eventFullDtos = events.stream().map(EventMapper::getEventFullDto).collect(Collectors.toList());

        for (EventFullDto event : eventFullDtos) {
            if (views.containsKey(event.getId())) {
                event.setViews(views.get(event.getId()));
            }
        }
        return eventFullDtos;
    }

    private Map<Long, Long> viewExtractor(List<Event> events, Event oldEvent) {
        LocalDateTime endSearching = LocalDateTime.now();
        LocalDateTime startSearching = LocalDateTime.now().plusHours(1);
        if (events != null) {
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
        }

        if (oldEvent != null) {
            startSearching = LocalDateTime.now().minusDays(1);
            endSearching = LocalDateTime.now().plusYears(100);
            events = List.of(oldEvent);
        }
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

        Map<Long, Long> views = viewExtractor(null, event);

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
