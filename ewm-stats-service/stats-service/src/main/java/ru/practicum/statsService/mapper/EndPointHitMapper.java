package ru.practicum.statsService.mapper;

import ru.practicum.statsDto.EndPointHitDto;
import ru.practicum.statsService.model.EndPointHit;

public class EndPointHitMapper {
    public static EndPointHitDto toEndPointHitDto(EndPointHit endPointHit) {
        EndPointHitDto dto = new EndPointHitDto();
        dto.setId(endPointHit.getId());
        dto.setApp(endPointHit.getApp());
        dto.setUri(endPointHit.getUri());
        dto.setIp(endPointHit.getIp());
        dto.setTimestamp(endPointHit.getTimestamp());
        return dto;
    }

    public static EndPointHit toEndPointHit(EndPointHitDto endPointHitDto) {
        EndPointHit endPointHit = new EndPointHit();
        endPointHit.setApp(endPointHitDto.getApp());
        endPointHit.setUri(endPointHitDto.getUri());
        endPointHit.setIp(endPointHitDto.getIp());
        endPointHit.setTimestamp(endPointHit.getTimestamp());
        return endPointHit;
    }

}
