package ru.practicum.statsDto;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class ViewStats {
    public String app;
    public String uri;
    public long hits;
}
