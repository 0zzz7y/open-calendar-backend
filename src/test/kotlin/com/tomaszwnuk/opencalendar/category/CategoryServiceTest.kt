package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryDto
import com.tomaszwnuk.opencalendar.domain.category.CategoryFilterDto
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.category.CategoryService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.argThat
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*
import java.util.Optional

@ExtendWith(MockitoExtension::class)
internal class CategoryServiceTest {

    @Mock
    private lateinit var _repository: CategoryRepository

    private lateinit var _service: CategoryService

    @BeforeEach
    fun setUp() {
        _service = CategoryService(_repository)
    }

    @Test
    fun `should return created category`() {
        val dto = CategoryDto(title = "Category", color = "#00FF00")
        val savedId = UUID.randomUUID()

        whenever(_repository.existsByTitle("Category")).thenReturn(false)
        whenever(_repository.save(any<Category>())).thenAnswer { invocation ->
            val arg = invocation.getArgument<Category>(0)
            arg.copy(id = savedId)
        }

        val result = _service.create(dto)

        assertNotNull(result.id)
        assertEquals(savedId, result.id)
        assertEquals("Category", result.title)
        assertEquals("#00FF00", result.color)

        verify(_repository).existsByTitle("Category")
        verify(_repository).save(argThat { title == "Category" && color == "#00FF00" })
    }

    @Test
    fun `should throw error when creating category with duplicate title`() {
        val dto = CategoryDto(title = "Duplicate Title", color = "#00FF00")

        whenever(_repository.existsByTitle("Duplicate Title")).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            _service.create(dto)
        }

        verify(_repository).existsByTitle("Duplicate Title")
        verify(_repository, never()).save(any<Category>())
    }

    @Test
    fun `should return all categories`() {
        val cat1 = Category(id = UUID.randomUUID(), title = "Work", color = "#00FF00")
        val cat2 = Category(id = UUID.randomUUID(), title = "Personal", color = "#0000FF")

        whenever(_repository.findAll()).thenReturn(listOf(cat1, cat2))

        val result = _service.getAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "Work" && it.color == "#00FF00" })
        assertTrue(result.any { it.title == "Personal" && it.color == "#0000FF" })

        verify(_repository).findAll()
    }

    @Test
    fun `should return category by id`() {
        val id = UUID.randomUUID()
        val category = Category(id = id, title = "Team", color = "#00FF00")

        whenever(_repository.findById(id)).thenReturn(Optional.of(category))

        val result = _service.getById(id)

        assertEquals(id, result.id)
        assertEquals("Team", result.title)
        assertEquals("#00FF00", result.color)

        verify(_repository).findById(id)
    }

    @Test
    fun `should throw error when category id not found`() {
        val id = UUID.randomUUID()

        whenever(_repository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.getById(id)
        }

        verify(_repository).findById(id)
    }

    @Test
    fun `should return list of filtered categories`() {
        val filter = CategoryFilterDto(title = "Filter", color = "#00FF00")
        val matching = Category(id = UUID.randomUUID(), title = "Filter", color = "#00FF00")

        whenever(_repository.filter("Filter", "#00FF00")).thenReturn(listOf(matching))

        val result = _service.filter(filter)

        assertEquals(1, result.size)
        assertEquals("Filter", result[0].title)
        assertEquals("#00FF00", result[0].color)

        verify(_repository).filter("Filter", "#00FF00")
    }

    @Test
    fun `should return updated category with old title`() {
        val id = UUID.randomUUID()
        val existing = Category(id = id, title = "Old", color = "#00FF00")
        val dto = CategoryDto(id = id, title = "Old", color = "#FF0000")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existing))
        whenever(_repository.save(any<Category>())).thenAnswer { it.getArgument<Category>(0) }

        val result = _service.update(id, dto)

        assertEquals("Old", result.title)
        assertEquals("#FF0000", result.color)

        verify(_repository).findById(id)
        verify(_repository, never()).existsByTitle(any<String>())
        verify(_repository).save(argThat { color == "#FF0000" })
    }

    @Test
    fun `should return updated category with new title`() {
        val id = UUID.randomUUID()
        val existing = Category(id = id, title = "Old", color = "#00FF00")
        val dto = CategoryDto(id = id, title = "New", color = "#FF0000")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existing))
        whenever(_repository.existsByTitle("New")).thenReturn(false)
        whenever(_repository.save(any<Category>())).thenAnswer { it.getArgument<Category>(0) }

        val result = _service.update(id, dto)

        assertEquals("New", result.title)
        assertEquals("#FF0000", result.color)

        verify(_repository).findById(id)
        verify(_repository).existsByTitle("New")
        verify(_repository).save(argThat { title == "New" && color == "#FF0000" })
    }

    @Test
    fun `should throw error when updating to duplicate title`() {
        val id = UUID.randomUUID()
        val existing = Category(id = id, title = "Old", color = "#00FF00")
        val dto = CategoryDto(id = id, title = "Duplicate", color = "#FF0000")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existing))
        whenever(_repository.existsByTitle("Duplicate")).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            _service.update(id, dto)
        }

        verify(_repository).findById(id)
        verify(_repository).existsByTitle("Duplicate")
        verify(_repository, never()).save(any<Category>())
    }

    @Test
    fun `should delete category when exists`() {
        val id = UUID.randomUUID()
        val existing = Category(id = id, title = "ToDelete", color = "#00FF00")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existing))
        doNothing().whenever(_repository).delete(existing)

        _service.delete(id)

        verify(_repository).findById(id)
        verify(_repository).delete(existing)
    }

    @Test
    fun `should throw error when deleting non existing category`() {
        val id = UUID.randomUUID()

        whenever(_repository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.delete(id)
        }

        verify(_repository).findById(id)
        verify(_repository, never()).delete(any<Category>())
    }
}
