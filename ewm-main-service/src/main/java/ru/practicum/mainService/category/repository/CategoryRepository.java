package ru.practicum.mainService.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainService.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
