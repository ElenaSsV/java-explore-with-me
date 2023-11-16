package ru.practicum.statsService.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "hits")
public class EndPointHit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "app", nullable = false)
    public String app;
    @Column(name = "uri", nullable = false)
    public String uri;
    @Column(name = "ip", nullable = false)
    public String ip;
    @Column(name = "created", nullable = false)
    public LocalDateTime timestamp = LocalDateTime.now();

}
