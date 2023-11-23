package ru.practicum.mainService.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainService.category.dto.CategoryDto;
import ru.practicum.mainService.category.service.CategoryService;


import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategoriesByAnyUser(@RequestParam(name = "from", required = false, defaultValue = "0") int from,
                                                       @RequestParam(name = "size", required = false, defaultValue = "10") int size) {
       log.info("Received request to get categories with the following params: from={}, size={}", from, size);
        PageRequest page = PageRequest.of(from, size);
        return categoryService.getAllCategoriesByAnyUser(page);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryByIdAnyUser(@PathVariable Long catId) {
        log.info("Received request to get category with the following param: catId={}", catId);
        return categoryService.getCategoryByIdByAnyUser(catId);
    }

}
