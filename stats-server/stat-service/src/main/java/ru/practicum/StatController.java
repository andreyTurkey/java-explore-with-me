package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
public class StatController {

    private final StatService statService;

    @PostMapping(path = "/hit")
    public ResponseEntity<HitDto> addHit(@Valid @RequestBody HitDto body) {
        log.debug(body + " - Пришел запрос на добавление");
        statService.addHit(body);
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    @GetMapping(path = "/stats")
    public List<ViewStatsDto> getBookingByIdByUserId(
            @RequestParam(value = "start") String start,
            @RequestParam(value = "end") String end,
            @RequestParam(value = "uris", required = false) List<String> uris,
            @RequestParam(value = "unique", defaultValue = "false") String unique) {
        log.debug(" - Пришел запрос на получение статистики - {}, {}", uris, unique);
        return statService.getViewStat(start, end, uris, unique);
    }
}
