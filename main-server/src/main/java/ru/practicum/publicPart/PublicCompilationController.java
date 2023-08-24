package ru.practicum.publicPart;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.CompilationDto;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping(path = "/compilations")
@AllArgsConstructor
public class PublicCompilationController {

    private final PublicService publicService;

    @GetMapping
    public List<CompilationDto> getCompilationByParameters(@RequestParam(value = "pinned", required = false) Boolean pinned,
                                                           @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.debug("Запрос подборок c параметрами: pinned {}, from {}, size {} ",pinned, from, size);
        return publicService.getCompilationByParameters(pinned, from, size);
    }

    @GetMapping(value = "{compId}")
    public CompilationDto getCompilationById(@Positive @PathVariable("compId") Long compId) {
        log.debug("Запрос подборки ID = {}", compId);
        return publicService.getCompilationById(compId);
    }
}
