package ru.practicum.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SubscriptionDto {

    Long id;

    Long subscriber;

    Long initiator;
}
