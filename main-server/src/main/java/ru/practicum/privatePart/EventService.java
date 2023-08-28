package ru.practicum.privatePart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewEventDto;
import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.exception.DuplicationException;
import ru.practicum.exception.NotAvailableException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.ParticipationRequestMapper;
import ru.practicum.model.*;
import ru.practicum.repository.*;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventService {

    EventRepository eventRepository;

    UserRepository userRepository;

    CategoryRepository categoryRepository;

    LocationRepository locationRepository;

    ParticipationRequestRepository participationRequestRepository;

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional
    public EventFullDto addEvent(NewEventDto newEventDto, Long userId) {
        if (LocalDateTime.parse(newEventDto.getEventDate(), dateTimeFormatter).isBefore(LocalDateTime.now().plusHours(2))) {
            throw new NotAvailableException("Дата и время события указаны менее, чем за 2 часа до текущего момента");
        }
        if (newEventDto.getPaid() == null) {
            newEventDto.setPaid(false);
        }
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }

        if (newEventDto.getState() == null) {
            newEventDto.setState(State.PENDING);
        }
        if (newEventDto.getRequestModeration() == null) {
            newEventDto.setRequestModeration(true);
            newEventDto.setState(State.PUBLISHED);
        }

        newEventDto.setAvailable(true);
        newEventDto.setConfirmedRequests(0);

        User initiator = userRepository.getReferenceById(userId);
        Category category = categoryRepository.getReferenceById(newEventDto.getCategory());

        locationRepository.save(newEventDto.getLocation());
        if (newEventDto.getParticipantLimit() == null) {
            newEventDto.setParticipantLimit(0);
        }
        eventRepository.save(EventMapper.getEvent(newEventDto, initiator, category));
        return EventMapper.getEventFullDto(eventRepository.getEventByAnnotation(newEventDto.getAnnotation()));
    }

    @Transactional
    public CreateParticipationAnswer addRequest(Long userId, Long eventId) {
        if (participationRequestRepository.existsByRequesterId(userId)) {
            throw new DuplicationException("Запрос уже существует.");
        }
        if (eventId < 1) {
            throw new DuplicationException("ID события должно быть больше 0");
        }
        Event event = eventRepository.getReferenceById(eventId);
        if (event.getInitiator().getId().equals(userId)) {
            throw new DuplicationException("Пользователь автор события");
        }
        if (event.getState() != State.PUBLISHED) {
            throw new DuplicationException("Событие еще не опубликовано");
        }
        User user = userRepository.getReferenceById(userId);
        ParticipationRequest pr = ParticipationRequest.builder()
                .event(event)
                .requester(user)
                .created(LocalDateTime.now())
                .build();

        if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
            pr.setStatus(State.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        } else {
            pr.setStatus(State.PENDING);
        }

        if (!(event.getParticipantLimit() == 0)) {
            if (event.getConfirmedRequests().equals(event.getParticipantLimit())) {
                throw new DuplicationException("Лимит на участие исчерпан при добавлении нового запроса");
            } else {
                pr.setStatus(State.PENDING);
            }
        }

        try {
            participationRequestRepository.save(pr);
        } catch (Exception ex) {
            throw new DuplicationException("Запрос уже существует");
        }

        eventRepository.save(event);

        ParticipationRequest newPr = participationRequestRepository
                .findByEvent_IdAndRequester_Id(event.getId(), user.getId());
        CreateParticipationAnswer cpa = CreateParticipationAnswer.builder()
                .id(newPr.getId())
                .requester(newPr.getRequester().getId())
                .created(newPr.getCreated())
                .event(newPr.getEvent().getId())
                .status(newPr.getStatus())
                .build();
        return cpa;
    }

    @Transactional
    public Map<String, List<ParticipationRequestDto>> changeRequestStatus(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest,
                                                                          Long userId,
                                                                          Long eventId) {
        List<ParticipationRequest> requests = participationRequestRepository
                .findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        Event event = eventRepository.getReferenceById(eventId);

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        if (event.getParticipantLimit().equals(event.getConfirmedRequests())) {
            throw new DuplicationException("Лимит на участие исчерпан.");
        }

        if (!event.getConfirmedRequests().equals(event.getParticipantLimit())
                && (eventRequestStatusUpdateRequest.getStatus().equals(State.CONFIRMED))) {
            confirmedRequests = requests
                    .stream()
                    .filter(o -> o.getStatus().equals(State.PENDING))
                    .collect(Collectors.toList());
            confirmedRequests.forEach(o -> o.setStatus(State.CONFIRMED));
            participationRequestRepository.saveAll(confirmedRequests);
            event.setConfirmedRequests(event.getConfirmedRequests() + confirmedRequests.size());
            eventRepository.save(event);
        }
        if (event.getConfirmedRequests().equals(event.getParticipantLimit())
                || eventRequestStatusUpdateRequest.getStatus().equals(State.REJECTED)) {
            rejectedRequests = requests
                    .stream()
                    .filter(o -> o.getStatus().equals(State.PENDING))
                    .collect(Collectors.toList());
            rejectedRequests.forEach(o -> o.setStatus(State.REJECTED));
            participationRequestRepository.saveAll(rejectedRequests);
        }

        Map<String, List<ParticipationRequestDto>> answer = new HashMap<>();
        answer.put("confirmedRequests", confirmedRequests
                .stream()
                .map(ParticipationRequestMapper::getParticipationRequestDto)
                .collect(Collectors.toList()));
        answer.put("rejectedRequests", rejectedRequests
                .stream()
                .map(ParticipationRequestMapper::getParticipationRequestDto)
                .collect(Collectors.toList()));

        return answer;
    }

    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        throwExceptionIfRequestNotFound(requestId);
        throwExceptionIfUserNotFound(userId);

        ParticipationRequest pr = participationRequestRepository.getReferenceById(requestId);
        pr.setStatus(State.CANCELED);
        return ParticipationRequestMapper.getParticipationRequestDto(pr);
    }

    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        if (from != null && size != null) {
            Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, sortById);
            return eventRepository.getEventByInitiatorIdAndPageable(userId, page)
                    .stream()
                    .map(EventMapper::getEventShortDto)
                    .collect(Collectors.toList());
        } else {
            return eventRepository.getEventByInitiatorId(userId)
                    .stream()
                    .map(EventMapper::getEventShortDto)
                    .collect(Collectors.toList());
        }
    }

    public EventFullDto getEventsByUserIdAndEventId(Long userId, Long eventId) {
        return EventMapper.getEventFullDto(eventRepository.findAllByInitiatorIdAndId(userId, eventId));
    }

    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        return participationRequestRepository.findAllByRequesterId(userId)
                .stream()
                .map(ParticipationRequestMapper::getParticipationRequestDto)
                .collect(Collectors.toList());
    }

    public List<ParticipationRequestDto> getRequestsOwnEvents(Long userId, Long eventId) {
        return participationRequestRepository.findAllByEventId(eventId)
                .stream()
                .map(ParticipationRequestMapper::getParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto changeEventsByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest eventUpdateDto) {
        Event oldEvent = eventRepository.getReferenceById(eventId);
        if (eventUpdateDto.getPaid() != null) {
            oldEvent.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getCategory() != null) {
            Category category = categoryRepository.getReferenceById(eventUpdateDto.getCategory());
            oldEvent.setCategory(category);
        }
        if (eventUpdateDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventUpdateDto.getEventDate(), dateTimeFormatter).isBefore(LocalDateTime.now()
                    .plusHours(2))) {
                throw new NotAvailableException("Дата и время события указаны менее, чем за 2 часа до текущего момента");
            }
            if (oldEvent.getState().equals(State.PUBLISHED)) {
                throw new DuplicationException("Событие уже опубликовано.");
            }
            oldEvent.setEventDate(LocalDateTime.parse(eventUpdateDto.getEventDate(), dateTimeFormatter));
        }
        if (eventUpdateDto.getDescription() != null) {
            oldEvent.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getLocation() != null) {
            oldEvent.setLocation(eventUpdateDto.getLocation());
        }
        if (eventUpdateDto.getAnnotation() != null) {
            oldEvent.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getTitle() != null) {
            oldEvent.setTitle(eventUpdateDto.getTitle());
        }

        if (eventUpdateDto.getStateAction() != null) {
            if (eventUpdateDto.getStateAction().equals(State.SEND_TO_REVIEW)) {
                oldEvent.setState(State.PENDING);
            } else if (eventUpdateDto.getStateAction().equals(State.CANCEL_REVIEW)) {
                oldEvent.setState(State.CANCELED);
            }
        }
        eventRepository.save(oldEvent);

        return EventMapper.getEventFullDto(oldEvent);
    }

    private void throwExceptionIfUserNotFound(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("Пользователь ID = "
                + userId + " не найден."));
    }

    private void throwExceptionIfRequestNotFound(Long requestId) {
        participationRequestRepository.findById(requestId).orElseThrow(() -> new EntityNotFoundException("Запрос ID = "
                + requestId + " не найдено."));
    }
}
