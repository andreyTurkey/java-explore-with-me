package ru.practicum;

public class HitMapper {

    public static HitDto getHitDto(Hit hit) {
        HitDto hitDto = new HitDto();
        hitDto.setApp(hit.getApp());
        hitDto.setUri(hit.getUri());
        hitDto.setTimestamp(hit.getTimestamp());
        hitDto.setIp(hit.getIp());
        return hitDto;
    }

    public static Hit getHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setTimestamp(hitDto.getTimestamp());
        hit.setIp(hitDto.getIp());
        return hit;
    }
}