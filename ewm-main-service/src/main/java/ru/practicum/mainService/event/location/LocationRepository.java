package ru.practicum.mainService.event.location;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainService.event.location.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
