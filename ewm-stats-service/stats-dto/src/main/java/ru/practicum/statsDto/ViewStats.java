package ru.practicum.statsDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ViewStats {
    public String app;
    public String uri;
    public long hits;
}
