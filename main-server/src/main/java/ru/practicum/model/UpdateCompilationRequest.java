package ru.practicum.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class UpdateCompilationRequest {

    List<Long> events;

    Boolean pinned;

    @Size(min = 1, max = 50, message = "Проверьте длину строки")
    String title;
}
