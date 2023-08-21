package ru.practicum.mapper;

import ru.practicum.dto.ParticipationRequestDto;
import ru.practicum.model.ParticipationRequest;

public class ParticipationRequestMapper {

    public static ParticipationRequestDto getParticipationRequestDto(ParticipationRequest pr) {
        ParticipationRequestDto prdto = ParticipationRequestDto.builder()
                .id(pr.getId())
                .requester(pr.getRequester().getId())
                .created(pr.getCreated())
                .event(pr.getEvent().getId())
                .status(pr.getStatus())
                .build();
        return prdto;
    }
}
