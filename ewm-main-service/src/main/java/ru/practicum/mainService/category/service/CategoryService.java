package ru.practicum.mainService.category.service;
import org.springframework.data.domain.PageRequest;
import ru.practicum.mainService.category.dto.CategoryDto;
import ru.practicum.mainService.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto postNewCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(long catId);

    CategoryDto updateCategory(long catId, CategoryDto categoryDto);

    List<CategoryDto> getAllCategoriesByAnyUser(PageRequest page);

    CategoryDto getCategoryByIdByAnyUser(long catId);

}
