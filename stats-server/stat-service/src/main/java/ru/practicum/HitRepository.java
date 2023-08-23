package ru.practicum;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("SELECT new ru.practicum.ViewStats (a.name, h.uri, count(distinct h.ip)) from Hit h join App a on h.app = a.id " +
            "WHERE h.uri in (:uris) AND h.timestamp between :start AND :end group by h.uri, a.name")
    List<ViewStats> findAllByUriWithDistinct(@Param("uris") Collection<String> uris,
                                             @Param("start") LocalDateTime startDecoded,
                                             @Param("end") LocalDateTime endDecoded);

    @Query("SELECT new ru.practicum.ViewStats (a.name, h.uri, count(h.ip)) from Hit h join App a on h.app = a.id " +
            "WHERE h.uri in (:uris) AND h.timestamp between :start AND :end group by h.uri, a.name")
    List<ViewStats> findAllByUriWithoutDistinct(@Param("uris") Collection<String> uris,
                                                @Param("start") LocalDateTime startDecoded,
                                                @Param("end") LocalDateTime endDecoded);

    @Query("SELECT new ru.practicum.ViewStats (a.name, h.uri, count(h.ip)) from Hit h join App a on h.app = a.id " +
            "WHERE  h.timestamp between :start AND :end group by h.uri, a.name")
    List<ViewStats> findAllWithoutUrisWithoutDistinct(@Param("start") LocalDateTime startDecoded,
                                                      @Param("end") LocalDateTime endDecoded);

    @Query("SELECT new ru.practicum.ViewStats (a.name, h.uri, count(distinct h.ip)) from Hit h join App a on h.app = a.id " +
            "WHERE  h.timestamp between :start AND :end group by h.uri, a.name")
    List<ViewStats> findAllWithoutUriDistinct(@Param("start") LocalDateTime startDecoded,
                                              @Param("end") LocalDateTime endDecoded);
}
