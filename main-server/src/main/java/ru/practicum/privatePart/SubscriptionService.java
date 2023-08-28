package ru.practicum.privatePart;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.SubscriptionDto;
import ru.practicum.repository.UserRepository;

@Transactional(readOnly = true)
@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscriptionService {

    UserRepository userRepository;

    public SubscriptionDto addSubscription(Long subscriberId, Long initiatorId) {
        return null;
    }


}
