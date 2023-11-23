package ru.practicum.mainService.event.model;

import lombok.Data;
import org.hibernate.annotations.Formula;
import ru.practicum.mainService.category.model.Category;
import ru.practicum.mainService.event.location.Location;
import ru.practicum.mainService.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "events")
public class Event {

    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @Formula("(SELECT count(r.id) from requests r where r.event_id=id AND r.status='CONFIRMED')")
    private int confirmedRequests = 0;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn = LocalDateTime.now();
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @Column(name = "paid", nullable = false)
    private Boolean paid = false;
    @Column(name = "published_on")
    private LocalDateTime publishedOn;
    @Column(name = "request_moderation", nullable = false)
    private boolean requestModeration = true;
    @Enumerated(EnumType.STRING)
    private State state = State.PENDING;
    @Column(name = "title", nullable = false)
    private String title;
    @Transient
    private long views = 0;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "location_id")
    private Location location;
    @Column(name = "participant_limit", nullable = false)
    private int participantLimit = 0;

}
