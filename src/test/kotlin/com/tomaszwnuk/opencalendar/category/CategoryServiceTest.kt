package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
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
    private lateinit var categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var categoryService: CategoryService

    private lateinit var sampleCategory: Category

    private lateinit var sampleCategoryDto: CategoryDto

    private lateinit var pageable: Pageable

    @BeforeEach
    fun setup() {
        sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Personal",
            color = CategoryColorHelper.toHex(Color.GREEN)
        )
        sampleCategoryDto = sampleCategory.toDto()
        pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created category`() {
        whenever(categoryRepository.existsByTitle(sampleCategoryDto.title)).thenReturn(false)
        doReturn(sampleCategory).whenever(categoryRepository).save(any())

        val result: Category = categoryService.create(sampleCategoryDto)

        assertNotNull(result)
        assertEquals(sampleCategory.id, result.id)
        assertEquals(sampleCategory.title, result.title)
        assertEquals(sampleCategory.color, result.color)

        verify(categoryRepository).save(any())
    }

    @Test
    fun `should return paginated list of all categories`() {
        val categories: List<Category> = listOf(
            sampleCategory,
            sampleCategory.copy(id = UUID.randomUUID()),
            sampleCategory.copy(id = UUID.randomUUID())
        )
        whenever(categoryRepository.findAll(pageable)).thenReturn(PageImpl(categories))

        val result: Page<Category> = categoryService.getAll(pageable)

        assertEquals(categories.size, result.totalElements.toInt())
        assertEquals(categories.map { it.id }, result.content.map { it.id })
        assertEquals(categories.map { it.title }, result.content.map { it.title })

        verify(categoryRepository).findAll(pageable)
    }

    @Test
    fun `should return category by id`() {
        val id: UUID = sampleCategory.id
        whenever(categoryRepository.findById(id)).thenReturn(Optional.of(sampleCategory))

        val result: Category = categoryService.getById(id)

        assertNotNull(result)
        assertEquals(sampleCategory.id, result.id)
        assertEquals(sampleCategory.title, result.title)
        assertEquals(sampleCategory.color, result.color)

        verify(categoryRepository).findById(id)
    }

    @Test
    fun `should return paginated list of filtered categories`() {
        val filter = CategoryFilterDto(title = "Personal")
        val categories: List<Category> = listOf(
            sampleCategory,
            sampleCategory.copy(id = UUID.randomUUID()),
            sampleCategory.copy(id = UUID.randomUUID())
        )
        whenever(categoryRepository.filter(eq(filter.title), isNull(), eq(pageable)))
            .thenReturn(PageImpl(categories))

        val result: Page<Category> = categoryService.filter(filter, pageable)

        assertEquals(categories.size, result.totalElements.toInt())
        assertEquals(categories.map { it.title }, result.content.map { it.title })

        verify(categoryRepository).filter(eq(filter.title), isNull(), eq(pageable))
    }

    @Test
    fun `should return updated category`() {
        val id: UUID = sampleCategory.id
        val updatedCategory: Category = sampleCategory.copy(title = "Work")

        whenever(categoryRepository.findById(id)).thenReturn(Optional.of(sampleCategory))
        whenever(categoryRepository.existsByTitle(updatedCategory.title)).thenReturn(false)
        doReturn(updatedCategory).whenever(categoryRepository).save(any())

        val result: Category = categoryService.update(id, updatedCategory.toDto())

        assertNotNull(result)
        assertEquals(updatedCategory.id, result.id)
        assertEquals("Work", result.title)
        assertEquals(updatedCategory.color, result.color)

        verify(categoryRepository).save(any())
    }

    @Test
    fun `should delete category by id`() {
        val id: UUID = sampleCategory.id
        whenever(categoryRepository.findById(id)).thenReturn(Optional.of(sampleCategory))
        doNothing().whenever(categoryRepository).delete(sampleCategory)

        categoryService.delete(id)

        verify(categoryRepository).delete(sampleCategory)
    }

}
