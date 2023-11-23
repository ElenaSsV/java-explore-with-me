package ru.practicum.mainService.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainService.category.dto.CategoryDto;
import ru.practicum.mainService.category.mapper.CategoryMapper;
import ru.practicum.mainService.category.dto.NewCategoryDto;
import ru.practicum.mainService.category.model.Category;
import ru.practicum.mainService.exception.DataConflictException;
import ru.practicum.mainService.exception.NotFoundException;
import ru.practicum.mainService.category.repository.CategoryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(rollbackFor = {DataConflictException.class})
    public CategoryDto postNewCategory(NewCategoryDto newCategoryDto) {
        log.info("Posting new category={}", newCategoryDto);
        Category categoryToPost = CategoryMapper.toCategory(newCategoryDto);

        Category postedCategory;
        try {
            postedCategory = categoryRepository.saveAndFlush(categoryToPost);
            log.info("Posted category={}", postedCategory);

        } catch (DataIntegrityViolationException e) {
            throw new DataConflictException("Category name should be unique.");
        }
        return CategoryMapper.toCategoryDto(postedCategory);
    }

    @Override
    public void deleteCategoryById(long catId) {
        log.info("Deleting category with id={}", catId);
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException("Category with id= " + catId + " is not found.");
        }
        try {
            categoryRepository.deleteById(catId);
        } catch (DataIntegrityViolationException e) {
            throw new DataConflictException("There are events in this category");
        }
    }

    @Override
    @Transactional(rollbackFor = {DataConflictException.class})
    public CategoryDto updateCategory(long catId, CategoryDto categoryDto) {
        log.info("Updating category with id={}, by {}", catId, categoryDto);

        Category catToUpdate = categoryRepository.findById(catId).orElseThrow(() ->
        new NotFoundException("Category with id=" + catId + " is not found"));

        if (categoryDto.getName() != null) {
            log.info("Setting name={}", categoryDto.getName());
            catToUpdate.setName(categoryDto.getName());
        }

        Category updatedCategory;
        try {
            updatedCategory = categoryRepository.saveAndFlush(catToUpdate);
        } catch (DataIntegrityViolationException e) {
            throw new DataConflictException("Category name should be unique.");
        }
        return CategoryMapper.toCategoryDto(updatedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategoriesByAnyUser(PageRequest pageRequest) {
        log.info("Retrieving categories via public API, pageRequest={}", pageRequest);

        return categoryRepository.findAll(pageRequest).stream()
                .map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryByIdByAnyUser(long catId) {
        log.info("Retrieving category with id={} via public API", catId);

        Category searchedCat = categoryRepository.findById(catId).orElseThrow(() ->
                new NotFoundException("Category with id=" + catId + " is not found"));
        return CategoryMapper.toCategoryDto(searchedCat);
    }
}
