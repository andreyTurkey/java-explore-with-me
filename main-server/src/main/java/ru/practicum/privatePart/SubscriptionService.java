package ru.practicum.privatePart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.SubscriptionDto;
import ru.practicum.exception.NotAvailableException;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.SubscriptionMapper;
import ru.practicum.model.Event;
import ru.practicum.model.State;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.SubscriptionRepository;
import ru.practicum.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionService {

    UserRepository userRepository;

    EventRepository eventRepository;

    SubscriptionRepository subscriptionRepository;

    @Transactional
    public SubscriptionDto addSubscription(Long subscriberId, Long initiatorId) {
        User initiator = userRepository.getReferenceById(initiatorId);
        throwException(initiatorId);
        SubscriptionDto subscriptionDto = SubscriptionDto.builder()
                .initiator(initiatorId)
                .subscriber(subscriberId)
                .build();
        subscriptionRepository.save(SubscriptionMapper.getSubscription(subscriptionDto,
                initiator, userRepository.getReferenceById(subscriberId)));
        return SubscriptionMapper.getSubscriptionDto(
                subscriptionRepository.findByInitiatorIdAndSubscriberId(initiatorId, subscriberId));
    }

    public List<EventShortDto> getAllSubscriptionEvents(Long subscriberId, Integer from, Integer size) {
        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, sortById);
        return subscriptionRepository.findBySubscriberId(subscriberId, State.PUBLISHED, page)
                .stream()
                .map(EventMapper::getEventShortDto)
                .collect(Collectors.toList());
    }

    public List<EventShortDto> getEventsByInitiatorId(Long subscriberId, Long initiatorId, Integer from, Integer size) {
        throwException(subscriberId);
        throwException(initiatorId);

        Sort sortById = Sort.by(Sort.Direction.DESC, "id");
        Pageable page = PageRequest.of(from > 0 ? from / size : 0, size, sortById);
        List<Event> events = eventRepository.findByInitiatorIdAndState(initiatorId, State.PUBLISHED, page).getContent();

        return events.stream()
                .map(EventMapper::getEventShortDto)
                .collect(Collectors.toList());
    }

    private void throwException(Long initiatorId) {
        if (!userRepository.getReferenceById(initiatorId).getSubscription()) {
            throw new NotAvailableException("Пользователь отменил подписку на свои события");
        }
    }
}

