package ru.practicum.admin.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.exception.DuplicationException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

import javax.persistence.EntityNotFoundException;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryServiceImpl implements CategoryService {

    CategoryRepository categoryRepository;

    EventRepository eventRepository;

    @Override
    @Transactional
    public Category addCategory(NewCategoryDto categoryDto) {
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new DuplicationException("Категория существует");
        }
        categoryRepository.save(CategoryMapper.getCategoryFromNew(categoryDto));
        return categoryRepository.findByName(categoryDto.getName());
    }

    @Override
    @Transactional
    public CategoryDto changeCategory(Integer catId, CategoryDto categoryDto) {
        if (categoryDto.getName() != null) {
            if (categoryRepository.existsByName(categoryDto.getName())) {
                Category categoryForCheckName = categoryRepository.findByName(categoryDto.getName());
                if (!categoryForCheckName.getId().equals(catId)) {
                    throw new DuplicationException("Категория существует");
                }
            } else {
                Category oldCategory = categoryRepository.getReferenceById(catId);
                oldCategory.setName(categoryDto.getName());
                categoryRepository.save(oldCategory);
            }
        } else {
            Category oldCategory = categoryRepository.getReferenceById(catId);
            oldCategory.setName(categoryDto.getName());
            categoryRepository.save(oldCategory);
        }
        return CategoryMapper.getCategoryDto(categoryRepository.getReferenceById(catId));
    }

    @Override
    @Transactional
    public void deleteCategory(Integer catId) {
        categoryRepository.findById(catId).orElseThrow(() -> new EntityNotFoundException(
                "Категория ID = " + catId + " не найдена."));
        if (eventRepository.existsByCategoryId(catId)) {
            throw new DuplicationException("Нельзя удалить категорию. Есть связанные события.");
        }
        categoryRepository.deleteById(catId);
    }
}
