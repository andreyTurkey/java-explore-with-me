package ru.practicum.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@NoArgsConstructor
@ToString
public class PublicParametersDto {

    String text;

    List<Integer> categories;

    Boolean paid;

    String rangeStart;

    String rangeEnd;

    Boolean onlyAvailable;

    String sort;

    Integer from;

    Integer size;

    HttpServletRequest request;
}
