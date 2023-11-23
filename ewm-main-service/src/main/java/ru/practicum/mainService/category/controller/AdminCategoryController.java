package ru.practicum.mainService.category.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.category.dto.CategoryDto;
import ru.practicum.mainService.category.dto.NewCategoryDto;
import ru.practicum.mainService.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
@Validated
@Slf4j
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto postNewCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Received request to post category: {}", newCategoryDto);
        return categoryService.postNewCategory(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable int catId) {
        log.info("Received request to delete category with id={}", catId);
        categoryService.deleteCategoryById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @RequestBody @Valid CategoryDto categoryDto) {
        log.info("Received request to update category with following params: catId={}, categoryDto={}", catId, categoryDto);
        return categoryService.updateCategory(catId, categoryDto);
    }




}
