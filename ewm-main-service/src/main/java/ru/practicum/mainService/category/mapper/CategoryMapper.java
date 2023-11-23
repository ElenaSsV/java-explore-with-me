package ru.practicum.mainService.category.mapper;


import ru.practicum.mainService.category.dto.CategoryDto;
import ru.practicum.mainService.category.dto.NewCategoryDto;
import ru.practicum.mainService.category.model.Category;

public class CategoryMapper {

    public static Category toCategory(NewCategoryDto categoryDto) {
        Category category = new Category();
        category.setName(categoryDto.getName());
        return category;
    }

    public static CategoryDto toCategoryDto(Category category) {
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }
}
