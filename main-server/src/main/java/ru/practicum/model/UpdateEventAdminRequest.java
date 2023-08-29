package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UpdateEventAdminRequest {

    @Size(min = 20, max = 2000, message = "Проверьте длину строки")
    String annotation;

    Integer categoryId;

    @Size(min = 20, max = 7000, message = "Проверьте длину строки")
    String description;

    String eventDate;

    Location location;

    Boolean paid;

    @Min(value = 0)
    Integer participantLimit;

    Boolean requestModeration;

    @Size(min = 3, max = 120, message = "Проверьте длину строки")
    String title;

    State stateAction;
}
