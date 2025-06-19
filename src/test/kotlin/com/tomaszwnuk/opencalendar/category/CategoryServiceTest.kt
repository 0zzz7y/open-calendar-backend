package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.domain.category.*
import com.tomaszwnuk.opencalendar.domain.user.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.awt.Color
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class CategoryServiceTest {

    @Mock
    private lateinit var _repository: CategoryRepository

    @Mock
    private lateinit var _userService: UserService

    private lateinit var _service: CategoryService

    private lateinit var _userId: UUID

    @BeforeEach
    fun setUp() {
        _service = CategoryService(_repository, _userService)
        _userId = UUID.randomUUID()
    }

    @Test
    fun `should return created category`() {
        val id: UUID = UUID.randomUUID()
        val dto = CategoryDto(
            id = id,
            name = "Test",
            color = CategoryColorHelper.DEFAULT_COLOR
        )

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.existsByNameAndUserId(name = any(), userId = any())).thenReturn(false)
        whenever(_repository.save(any<Category>())).thenReturn(
            Category(
                id = id,
                name = dto.name,
                color = CategoryColorHelper.DEFAULT_COLOR,
                userId = _userId
            )
        )

        val result: CategoryDto = _service.create(dto = dto)

        assertNotNull(result.id)
        assertEquals(dto.name, result.name)
        assertEquals(dto.color, result.color)

        verify(_repository).save(any())
    }

    @Test
    fun `should throw error when creating category with duplicate title`() {
        val dto = CategoryDto(
            id = UUID.randomUUID(),
            name = "Test",
            color = CategoryColorHelper.DEFAULT_COLOR
        )

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.existsByNameAndUserId(name = any(), userId = any())).thenReturn(true)

        assertThrows<IllegalArgumentException> { _service.create(dto = dto) }

        verify(_repository, never()).save(any())
    }

    @Test
    fun `should return all categories`() {
        val category = Category(
            id = UUID.randomUUID(),
            name = "Test",
            color = CategoryColorHelper.DEFAULT_COLOR,
            userId = _userId
        )
        val categories: List<Category> =
            listOf(category, category.copy(id = UUID.randomUUID()), category.copy(id = UUID.randomUUID()))

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findAllByUserId(userId = _userId)).thenReturn(categories)

        val result: List<CategoryDto> = _service.getAll()

        assertEquals(categories.size, result.size)
        assertEquals(result, categories.map { it.toDto() })

        verify(_repository).findAllByUserId(userId = _userId)
    }

    @Test
    fun `should return category by id`() {
        val id: UUID = UUID.randomUUID()
        val category = Category(
            id = id,
            name = "Test",
            color = CategoryColorHelper.DEFAULT_COLOR,
            userId = _userId
        )

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.of(category))

        val result: CategoryDto = _service.getById(id = id)

        assertEquals(category.toDto(), result)

        verify(_repository).findByIdAndUserId(id = id, userId = _userId)
    }

    @Test
    fun `should throw error when category id not found`() {
        val id: UUID = UUID.randomUUID()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.getById(id = id) }

        verify(_repository).findByIdAndUserId(id = id, userId = _userId)
    }

    @Test
    fun `should return list of filtered categories`() {
        val category = Category(
            id = UUID.randomUUID(),
            name = "Test",
            color = CategoryColorHelper.DEFAULT_COLOR,
            userId = _userId
        )
        val filter = CategoryFilterDto(
            name = "Test",
            color = null
        )
        val categories: List<Category> = listOf(category)

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.filter(userId = _userId, name = filter.name, color = filter.color)).thenReturn(categories)

        val result: List<CategoryDto> = _service.filter(filter = filter)

        assertEquals(result.size, categories.size)
        assertEquals(result, categories.map { it.toDto() })

        verify(_repository).filter(userId = _userId, name = filter.name, color = filter.color)
    }

    @Test
    fun `should return updated category with old title`() {
        val id = UUID.randomUUID()
        val existing = Category(
            id = id,
            name = "Test",
            color = CategoryColorHelper.DEFAULT_COLOR,
            userId = _userId
        )
        val dto = existing.copy(color = CategoryColorHelper.toHex(Color.GREEN)).toDto()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.of(existing))
        whenever(_repository.save(any<Category>())).thenAnswer { it.arguments[0] as Category }

        val result = _service.update(id = id, dto = dto)

        assertEquals(dto, result)

        verify(_repository, never()).existsByNameAndUserId(any(), any())
        verify(_repository).save(any())
    }

    @Test
    fun `should return updated category with new title`() {
        val id = UUID.randomUUID()
        val existing = Category(
            id = id,
            name = "Test",
            color = CategoryColorHelper.DEFAULT_COLOR,
            userId = _userId
        )
        val dto = existing.copy(name = "Test2").toDto()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.of(existing))
        whenever(_repository.existsByNameAndUserId(name = dto.name, userId = _userId)).thenReturn(false)
        whenever(_repository.save(any<Category>())).thenAnswer { it.arguments[0] as Category }

        val result = _service.update(id = id, dto = dto)

        assertEquals(dto, result)

        verify(_repository).existsByNameAndUserId(name = dto.name, userId = _userId)
        verify(_repository).save(any())
    }

    @Test
    fun `should throw error when updating to duplicate title`() {
        val id = UUID.randomUUID()
        val existing = Category(
            id = id,
            name = "Test",
            color = CategoryColorHelper.DEFAULT_COLOR,
            userId = _userId
        )
        val dto = existing.copy(name = "Test2").toDto()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.of(existing))
        whenever(_repository.existsByNameAndUserId(name = dto.name, userId = _userId)).thenReturn(true)

        assertThrows<IllegalArgumentException> { _service.update(id = id, dto = dto) }

        verify(_repository).existsByNameAndUserId(name = dto.name, userId = _userId)
        verify(_repository, never()).save(any())
    }

    @Test
    fun `should delete category when exists`() {
        val id: UUID = UUID.randomUUID()
        val existing = Category(
            id = id,
            name = "Test",
            color = CategoryColorHelper.DEFAULT_COLOR,
            userId = _userId
        )

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.of(existing))
        doNothing().whenever(_repository).delete(existing)

        _service.delete(id = id)

        verify(_repository).findByIdAndUserId(id = id, userId = _userId)
        verify(_repository).delete(existing)
    }

    @Test
    fun `should throw error when deleting non existing category`() {
        val id: UUID = UUID.randomUUID()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.delete(id = id) }

        verify(_repository).findByIdAndUserId(id = id, userId = _userId)
        verify(_repository, never()).delete(any<Category>())
    }

}
