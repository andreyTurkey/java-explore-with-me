package ru.practicum.mapper;

import ru.practicum.dto.SubscriptionDto;
import ru.practicum.model.Subscription;
import ru.practicum.model.User;

public class SubscriptionMapper {

    public static SubscriptionDto getSubscriptionDto(Subscription subscription) {
        SubscriptionDto subscriptionDto = SubscriptionDto.builder()
                .id(subscription.getId())
                .initiator(subscription.getInitiator().getId())
                .subscriber(subscription.getSubscriber().getId())
                .build();
        return subscriptionDto;
    }

    public static Subscription getSubscription(SubscriptionDto subscriptionDto,
                                               User initiator,
                                               User subscriber) {
        Subscription subscription = Subscription.builder()
                .id(subscriptionDto.getId())
                .initiator(initiator)
                .subscriber(subscriber)
                .build();
        return subscription;
    }
}

