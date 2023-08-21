package ru.practicum.admin.adminController;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.admin.AdminService;
import ru.practicum.dto.EventFullDto;
import ru.practicum.model.State;
import ru.practicum.model.UpdateEventAdminRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/events")
@AllArgsConstructor
public class AdminEventController {

    private final AdminService adminService;

    @PatchMapping(value = "/{eventId}")
    public ResponseEntity<Object> updateEvent(@Valid @RequestBody UpdateEventAdminRequest body,
                                              @Positive @PathVariable("eventId") Long eventId) {
        log.debug("Запрос на добавление нового события {} eventId - {}", body, eventId);
        return new ResponseEntity<>(adminService.updateEvent(body, eventId), HttpStatus.OK);
    }

    @GetMapping
    public List<EventFullDto> getEventByParameters(@RequestParam(value = "users", required = false) List<Long> users,
                                                   @RequestParam(value = "states", required = false) List<State> states,
                                                   @RequestParam(value = "categories", required = false) List<Integer> categories,
                                                   @RequestParam(value = "rangeStart", required = false) String rangeStart,
                                                   @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
                                                   @Min(0) @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                   @Min(1) @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.debug("Запрос событий по параметрам: пользователи {}, статусы {}, категории {}, старт {}, финиш {}, from {}, size {}"
                , users, states, categories, rangeStart, rangeEnd, from, size);
        return adminService.getEventsByParameter(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
