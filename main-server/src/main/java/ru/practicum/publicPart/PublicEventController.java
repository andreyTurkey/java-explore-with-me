package ru.practicum.publicPart;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EventFullDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class PublicEventController {

    private final PublicService publicService;

    @GetMapping
    public List<EventFullDto> getEventsByParameters(@RequestParam(value = "text", required = false) String text,
                                                    @RequestParam(value = "categories", required = false) List<Integer> categories,
                                                    @RequestParam(value = "paid", required = false) Boolean paid,
                                                    @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                                    @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                                    @RequestParam(value = "onlyAvailable", defaultValue = "false") Boolean onlyAvailable,
                                                    @RequestParam(value = "sort", required = false) String sort,
                                                    @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                    @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                    HttpServletRequest request) {
        log.debug("Запрос событий c параметрами: text {}, categories {}, paid {}, rangeStart {}, rangeEnd {}, onlyAvailable {}," +
                        " from {}, size {}"
                , text, categories, paid, rangeStart, rangeEnd, onlyAvailable, from, size);
        return publicService.getEventsByParameters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
    }

    @GetMapping(value = "{eventId}")
    public EventFullDto getEventById(@Positive @PathVariable("eventId") Long eventId,
                                     HttpServletRequest request) {
        log.debug("Запрос события c ID = {}", eventId);
        return publicService.getEventById(eventId, request);
    }
}
