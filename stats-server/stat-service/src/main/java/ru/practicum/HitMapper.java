package ru.practicum;

public class HitMapper {

    public static Hit getHit(HitDto hitDto, Integer appId) {
        Hit hit = new Hit();
        hit.setApp(appId);
        hit.setUri(hitDto.getUri());
        hit.setTimestamp(hitDto.getTimestamp());
        hit.setIp(hitDto.getIp());
        return hit;
    }
}