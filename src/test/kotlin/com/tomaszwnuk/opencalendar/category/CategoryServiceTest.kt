package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryColorHelper
import com.tomaszwnuk.opencalendar.domain.category.CategoryDto
import com.tomaszwnuk.opencalendar.domain.category.CategoryFilterDto
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.category.CategoryService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
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

    private lateinit var _sampleCategoryDto: CategoryDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Personal",
            color = CategoryColorHelper.toHex(Color.GREEN)
        )
        _sampleCategoryDto = _sampleCategory.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created category`() {
        whenever(_categoryRepository.existsByTitle(_sampleCategoryDto.title)).thenReturn(false)
        doReturn(_sampleCategory).whenever(_categoryRepository).save(any())

        val result: Category = _categoryService.create(_sampleCategoryDto)

        assertNotNull(result)
        assertEquals(_sampleCategory.id, result.id)
        assertEquals(_sampleCategory.title, result.title)
        assertEquals(_sampleCategory.color, result.color)

        verify(_categoryRepository).save(any())
    }

    @Test
    fun `should return paginated list of all categories`() {
        val categories: List<Category> = listOf(
            _sampleCategory,
            _sampleCategory.copy(id = UUID.randomUUID()),
            _sampleCategory.copy(id = UUID.randomUUID())
        )
        whenever(_categoryRepository.findAll(_pageable)).thenReturn(PageImpl(categories))

        val result: Page<Category> = _categoryService.getAll(_pageable)

        assertEquals(categories.size, result.totalElements.toInt())
        assertEquals(categories.map { it.id }, result.content.map { it.id })
        assertEquals(categories.map { it.title }, result.content.map { it.title })

        verify(_categoryRepository).findAll(_pageable)
    }

    @Test
    fun `should return category by id`() {
        val id: UUID = _sampleCategory.id
        whenever(_categoryRepository.findById(id)).thenReturn(Optional.of(_sampleCategory))

        val result: Category = _categoryService.getById(id)

        assertNotNull(result)
        assertEquals(_sampleCategory.id, result.id)
        assertEquals(_sampleCategory.title, result.title)
        assertEquals(_sampleCategory.color, result.color)

        verify(_categoryRepository).findById(id)
    }

    @Test
    fun `should return paginated list of filtered categories`() {
        val filter = CategoryFilterDto(title = "Personal")
        val categories: List<Category> = listOf(
            _sampleCategory,
            _sampleCategory.copy(id = UUID.randomUUID()),
            _sampleCategory.copy(id = UUID.randomUUID())
        )
        whenever(_categoryRepository.filter(eq(filter.title), isNull(), eq(_pageable)))
            .thenReturn(PageImpl(categories))

        val result: Page<Category> = _categoryService.filter(filter, _pageable)

        assertEquals(categories.size, result.totalElements.toInt())
        assertEquals(categories.map { it.title }, result.content.map { it.title })

        verify(_categoryRepository).filter(eq(filter.title), isNull(), eq(_pageable))
    }

    @Test
    fun `should return updated category`() {
        val id: UUID = _sampleCategory.id
        val updatedCategory: Category = _sampleCategory.copy(title = "Work")

        whenever(_categoryRepository.findById(id)).thenReturn(Optional.of(_sampleCategory))
        whenever(_categoryRepository.existsByTitle(updatedCategory.title)).thenReturn(false)
        doReturn(updatedCategory).whenever(_categoryRepository).save(any())

        val result: Category = _categoryService.update(id, updatedCategory.toDto())

        assertNotNull(result)
        assertEquals(updatedCategory.id, result.id)
        assertEquals("Work", result.title)
        assertEquals(updatedCategory.color, result.color)

        verify(_categoryRepository).save(any())
    }

    @Test
    fun `should delete category by id`() {
        val id: UUID = _sampleCategory.id
        whenever(_categoryRepository.findById(id)).thenReturn(Optional.of(_sampleCategory))
        doNothing().whenever(_categoryRepository).delete(_sampleCategory)

        _categoryService.delete(id)

        verify(_categoryRepository).delete(_sampleCategory)
    }

}
