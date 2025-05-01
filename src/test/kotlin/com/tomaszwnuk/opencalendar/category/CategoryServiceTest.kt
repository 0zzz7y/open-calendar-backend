/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.domain.category.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*

/**
 * Unit tests for the `CategoryService` class.
 * Verifies the behavior of the service methods using mocked dependencies.
 */
@ExtendWith(MockitoExtension::class)
internal class CategoryServiceTest {

    /**
     * Mocked instance of `CategoryRepository` for simulating database operations.
     */
    @Mock
    private lateinit var _repository: CategoryRepository

    /**
     * Instance of `CategoryService` under test.
     */
    private lateinit var _service: CategoryService

    /**
     * Sets up the test environment before each test.
     * Initializes the `CategoryService` with the mocked repository.
     */
    @BeforeEach
    fun setUp() {
        _service = CategoryService(_repository)
    }

    /**
     * Tests the creation of a category.
     * Verifies that the service returns the created category with a generated ID.
     */
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

    /**
     * Tests the creation of a category with a duplicate title.
     * Verifies that the service throws an `IllegalArgumentException`.
     */
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

    /**
     * Tests retrieving all categories.
     * Verifies that the service returns a list of all categories.
     */
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

    /**
     * Tests retrieving a category by its ID.
     * Verifies that the service returns the correct category.
     */
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

    /**
     * Tests retrieving a category by a non-existent ID.
     * Verifies that the service throws a `NoSuchElementException`.
     */
    @Test
    fun `should throw error when category id not found`() {
        val id = UUID.randomUUID()

        whenever(_repository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.getById(id)
        }

        verify(_repository).findById(id)
    }

    /**
     * Tests filtering categories based on criteria.
     * Verifies that the service returns a list of matching categories.
     */
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

    /**
     * Tests updating a category with the same title.
     * Verifies that the service updates the category and returns the updated entity.
     */
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

    /**
     * Tests updating a category with a new title.
     * Verifies that the service updates the category and returns the updated entity.
     */
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

    /**
     * Tests updating a category with a duplicate title.
     * Verifies that the service throws an `IllegalArgumentException`.
     */
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

    /**
     * Tests deleting a category that exists.
     * Verifies that the service deletes the category.
     */
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

    /**
     * Tests deleting a category that does not exist.
     * Verifies that the service throws a `NoSuchElementException`.
     */
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
