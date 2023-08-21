package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class EventRequestStatusUpdateRequest {

    List<Long> requestIds;

    State status;
}
