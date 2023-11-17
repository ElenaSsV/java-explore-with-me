package ru.practicum.statsDto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
public class EndPointHitDto {
    public Long id;
    @NotBlank
    public String app;
    @NotBlank
    public String uri;
    @NotBlank
    public String ip;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime timestamp;
}
