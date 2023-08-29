package ru.practicum.admin.service;

import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.model.Category;

public interface CategoryService {

    Category addCategory(NewCategoryDto categoryDto);

    CategoryDto changeCategory(Integer catId, CategoryDto categoryDto);

    void deleteCategory(Integer catId);
}
