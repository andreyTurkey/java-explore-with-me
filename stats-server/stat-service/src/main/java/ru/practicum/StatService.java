package ru.practicum;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatService {

    HitRepository hitRepository;

    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public HitDto addHit(HitDto hitDto) {
        hitRepository.save(HitMapper.getHit(hitDto));
        return hitDto;
    }

    public List<ViewStatsDto> getViewStat(String start, String end, List<String> uris, String unique) {
        LocalDateTime startDecoded = LocalDateTime.parse((URLDecoder.decode(start, StandardCharsets.UTF_8)),
                dateTimeFormatter);
        LocalDateTime endDecoded = LocalDateTime.parse((URLDecoder.decode(end, StandardCharsets.UTF_8)),
                dateTimeFormatter);

        //LocalDateTime startDecoded = LocalDateTime.parse(start, dateTimeFormatter);
        //LocalDateTime endDecoded = LocalDateTime.parse(end, dateTimeFormatter);

        if (unique.equals("false") && uris != null) {                       // Со списком не уникальный
            return hitRepository.findAllByUriWithoutDistinct(uris, startDecoded, endDecoded)
                    .stream()
                    .sorted((x1,x2) -> x2.getHits().compareTo(x1.getHits()))
                    .map(ViewStatMapper::getViewStatsDto)
                    .collect(Collectors.toList());
        }

        if (unique.equals("false")) {                                       // Без списка не уникальный
            return hitRepository.findAllWithoutUrisWithoutDistinct(startDecoded, endDecoded)
                    .stream()
                    .sorted((x1,x2) -> x2.getHits().compareTo(x1.getHits()))
                    .map(ViewStatMapper::getViewStatsDto)
                    .collect(Collectors.toList());
        }

        if (uris == null && unique.equals("true")) {                     // Без списка уникальный
            return hitRepository.findAllWithoutUriDistinct(startDecoded, endDecoded)
                    .stream()
                    .sorted((x1,x2) -> x2.getHits().compareTo(x1.getHits()))
                    .map(ViewStatMapper::getViewStatsDto)
                    .collect(Collectors.toList());
        }
        if (unique.equals("true")) {                                            // Со списком  уникальный
            return hitRepository.findAllByUriWithDistinct(uris, startDecoded, endDecoded)
                    .stream()
                    .sorted((x1,x2) -> x2.getHits().compareTo(x1.getHits()))
                    .map(ViewStatMapper::getViewStatsDto)
                    .collect(Collectors.toList());
        }
        return null;
    }
}
