package com.tomaszwnuk.dailyassistant.category

import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_SIZE
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.*
import org.mockito.quality.Strictness
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.awt.Color
import java.util.*

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryServiceTest {

    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var _categoryService: CategoryService

    private lateinit var _sampleCategory: Category

    private lateinit var _sampleDto: CategoryDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            name = "Personal",
            color = CategoryColors.toHex(Color.GREEN)
        )
        _sampleDto = _sampleCategory.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created category`() {
        whenever(_categoryRepository.existsByName(_sampleDto.name)).thenReturn(false)
        doReturn(_sampleCategory).whenever(_categoryRepository).save(any())
        val result: Category = _categoryService.create(_sampleDto)

        assertEquals(_sampleCategory.id, result.id)
        verify(_categoryRepository).save(any())
    }

    @Test
    fun `should return paginated list of categories`() {
        val categories: List<Category> = listOf(_sampleCategory, _sampleCategory, _sampleCategory)

        whenever(_categoryRepository.findAll(_pageable)).thenReturn(PageImpl(categories))
        val result: Page<Category> = _categoryService.getAll(_pageable)

        assertEquals(categories.size, result.totalElements.toInt())
        verify(_categoryRepository).findAll(_pageable)
    }

    @Test
    fun `should return category by id`() {
        val id: UUID = _sampleCategory.id

        whenever(_categoryRepository.findById(id)).thenReturn(Optional.of(_sampleCategory))
        val result: Category = _categoryService.getById(id)

        assertEquals(_sampleCategory.id, result.id)
        verify(_categoryRepository).findById(id)
    }

    @Test
    fun `should return filtered categories`() {
        val filter = CategoryFilterDto(name = "Personal")
        val categories: List<Category> = listOf(_sampleCategory, _sampleCategory, _sampleCategory)

        whenever(
            _categoryRepository.filter(
                eq(filter.name),
                isNull(),
                eq(_pageable)
            )
        ).thenReturn(PageImpl(categories))
        val result: Page<Category> = _categoryService.filter(filter, _pageable)

        assertEquals(categories.size, result.totalElements.toInt())
        verify(_categoryRepository).filter(
            eq(filter.name),
            isNull(),
            eq(_pageable)
        )
    }

    @Test
    fun `should return updated category`() {
        val id: UUID = _sampleCategory.id
        val updated = _sampleCategory.copy(name = "Work")

        whenever(_categoryRepository.findById(id)).thenReturn(Optional.of(_sampleCategory))
        whenever(_categoryRepository.existsByName(updated.name)).thenReturn(false)
        doReturn(updated).whenever(_categoryRepository).save(any())
        val result: Category = _categoryService.update(_sampleCategory.id, updated.toDto())

        assertEquals(updated.name, result.name)
        verify(_categoryRepository).save(any())
    }

    @Test
    fun `should delete category`() {
        val id: UUID = _sampleCategory.id

        whenever(_categoryRepository.findById(id)).thenReturn(Optional.of(_sampleCategory))
        doNothing().whenever(_categoryRepository).delete(_sampleCategory)
        _categoryService.delete(id)

        verify(_categoryRepository).delete(_sampleCategory)
    }

}
