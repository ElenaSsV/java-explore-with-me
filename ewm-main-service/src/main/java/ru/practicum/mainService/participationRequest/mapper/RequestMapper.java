package ru.practicum.mainService.participationRequest.mapper;

import ru.practicum.mainService.participationRequest.dto.ParticipationRequestDto;
import ru.practicum.mainService.participationRequest.model.ParticipationRequest;

public class RequestMapper {

    public static ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setCreated(request.getCreated());
        dto.setEvent(request.getEvent().getId());
        dto.setRequester(request.getRequester().getId());
        dto.setStatus(request.getStatus());
        return dto;
    }
}
