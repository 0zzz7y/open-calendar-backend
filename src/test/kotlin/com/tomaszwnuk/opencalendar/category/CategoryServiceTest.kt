package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.domain.category.*
import com.tomaszwnuk.opencalendar.domain.user.UserService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class CategoryServiceTest {

    @Mock
    private lateinit var _repository: CategoryRepository

    @Mock
    private lateinit var _userService: UserService

    private lateinit var _service: CategoryService

    @BeforeEach
    fun setUp() {
        _service = CategoryService(_repository, _userService)
    }

    @Test
    fun `should return created category`() {
        val dto = CategoryDto(name = "Category", color = "#00FF00")
        val savedId = UUID.randomUUID()

        whenever(_userService.getCurrentUserId()).thenReturn(UUID.randomUUID())
        val userId = _userService.getCurrentUserId()
        whenever(_repository.existsByNameAndUserId("Category", userId)).thenReturn(false)
        whenever(_repository.save(any<Category>())).thenAnswer { invocation ->
            val arg = invocation.getArgument<Category>(0)
            arg.copy(id = savedId)
        }

        val result = _service.create(dto)

        assertNotNull(result.id)
        assertEquals(savedId, result.id)
        assertEquals("Category", result.name)
        assertEquals("#00FF00", result.color)

        verify(_repository).existsByNameAndUserId("Category", userId)
        verify(_repository).save(argThat { name == "Category" && color == "#00FF00" })
    }

    @Test
    fun `should throw error when creating category with duplicate title`() {
        val dto = CategoryDto(name = "Duplicate Title", color = "#00FF00")

        whenever(_userService.getCurrentUserId()).thenReturn(UUID.randomUUID())
        val userId = _userService.getCurrentUserId()
        whenever(_repository.existsByNameAndUserId(name = "Duplicate Title", userId)).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            _service.create(dto = dto)
        }

        verify(_repository).existsByNameAndUserId(name = "Duplicate Title", userId)
        verify(_repository, never()).save(any<Category>())
    }

    @Test
    fun `should return all categories`() {
        whenever(_userService.getCurrentUserId()).thenReturn(UUID.randomUUID())
        val userId = _userService.getCurrentUserId()
        val cat1 = Category(id = UUID.randomUUID(), name = "Work", color = "#00FF00", userId = userId)
        val cat2 = Category(id = UUID.randomUUID(), name = "Personal", color = "#0000FF", userId = userId)

        whenever(_repository.findAllByUserId(userId)).thenReturn(listOf(cat1, cat2))

        val result = _service.getAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Work" && it.color == "#00FF00" })
        assertTrue(result.any { it.name == "Personal" && it.color == "#0000FF" })

        verify(_repository).findAll()
    }

    @Test
    fun `should return category by id`() {
        val id = UUID.randomUUID()
        val category = Category(id = id, name = "Team", color = "#00FF00")

        whenever(_repository.findById(id)).thenReturn(Optional.of(category))

        val result = _service.getById(id = id)

        assertEquals(id, result.id)
        assertEquals("Team", result.name)
        assertEquals("#00FF00", result.color)

        verify(_repository).findById(id)
    }

    @Test
    fun `should throw error when category id not found`() {
        val id = UUID.randomUUID()

        whenever(_repository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.getById(id = id)
        }

        verify(_repository).findById(id)
    }

    @Test
    fun `should return list of filtered categories`() {
        val filter = CategoryFilterDto(name = "Filter", color = "#00FF00")
        val matching = Category(id = UUID.randomUUID(), name = "Filter", color = "#00FF00")

        whenever(_repository.filter(name = "Filter", color = "#00FF00")).thenReturn(listOf(matching))

        val result = _service.filter(filter = filter)

        assertEquals(1, result.size)
        assertEquals("Filter", result[0].name)
        assertEquals("#00FF00", result[0].color)

        verify(_repository).filter(name = "Filter", color = "#00FF00")
    }

    @Test
    fun `should return updated category with old title`() {
        val id = UUID.randomUUID()
        val existing = Category(id = id, name = "Old", color = "#00FF00")
        val dto = CategoryDto(id = id, name = "Old", color = "#FF0000")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existing))
        whenever(_repository.save(any<Category>())).thenAnswer { it.getArgument<Category>(0) }

        val result = _service.update(id = id, dto = dto)

        assertEquals("Old", result.name)
        assertEquals("#FF0000", result.color)

        verify(_repository).findById(id)
        verify(_repository, never()).existsByNameAndUserId(any<String>())
        verify(_repository).save(argThat { color == "#FF0000" })
    }

    @Test
    fun `should return updated category with new title`() {
        val id = UUID.randomUUID()
        val existing = Category(id = id, name = "Old", color = "#00FF00")
        val dto = CategoryDto(id = id, name = "New", color = "#FF0000")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existing))
        whenever(_repository.existsByNameAndUserId("New")).thenReturn(false)
        whenever(_repository.save(any<Category>())).thenAnswer { it.getArgument<Category>(0) }

        val result = _service.update(id = id, dto = dto)

        assertEquals("New", result.name)
        assertEquals("#FF0000", result.color)

        verify(_repository).findById(id)
        verify(_repository).existsByNameAndUserId(name = "New")
        verify(_repository).save(argThat { name == "New" && color == "#FF0000" })
    }

    @Test
    fun `should throw error when updating to duplicate title`() {
        val id = UUID.randomUUID()
        val existing = Category(id = id, name = "Old", color = "#00FF00")
        val dto = CategoryDto(id = id, name = "Duplicate", color = "#FF0000")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existing))
        whenever(_repository.existsByNameAndUserId(name = "Duplicate")).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            _service.update(id = id, dto = dto)
        }

        verify(_repository).findById(id)
        verify(_repository).existsByNameAndUserId(name = "Duplicate")
        verify(_repository, never()).save(any<Category>())
    }

    @Test
    fun `should delete category when exists`() {
        val id = UUID.randomUUID()
        val existing = Category(id = id, name = "ToDelete", color = "#00FF00")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existing))
        doNothing().whenever(_repository).delete(existing)

        _service.delete(id = id)

        verify(_repository).findById(id)
        verify(_repository).delete(existing)
    }

    @Test
    fun `should throw error when deleting non existing category`() {
        val id = UUID.randomUUID()

        whenever(_repository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.delete(id = id)
        }

        verify(_repository).findById(id)
        verify(_repository, never()).delete(any<Category>())
    }

}
