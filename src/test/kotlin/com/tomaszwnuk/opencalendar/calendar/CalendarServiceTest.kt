package com.tomaszwnuk.opencalendar.calendar

import com.tomaszwnuk.opencalendar.domain.calendar.*
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.user.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class CalendarServiceTest {

    @Mock
    private lateinit var _repository: CalendarRepository

    @Mock
    private lateinit var _userService: UserService

    private lateinit var _service: CalendarService

    private lateinit var _userId: UUID

    @BeforeEach
    fun setUp() {
        _service = CalendarService(_repository, _userService)
        _userId = UUID.randomUUID()
    }

    @Test
    fun `should return created calendar`() {
        val id: UUID = UUID.randomUUID()
        val dto = CalendarDto(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢"
        )

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.existsByNameAndUserId(name = any(), userId = any())).thenReturn(false)
        whenever(_repository.save(any<Calendar>())).thenReturn(
            Calendar(
                id = id,
                name = dto.name,
                emoji = dto.emoji,
                userId = _userId
            )
        )

        val result: CalendarDto = _service.create(dto = dto)

        assertNotNull(result.id)
        assertEquals(dto.name, result.name)
        assertEquals(dto.emoji, result.emoji)

        verify(_repository).save(any())
    }

    @Test
    fun `should throw error when creating calendar with duplicate title`() {
        val dto = CalendarDto(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢"
        )

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.existsByNameAndUserId(name = any(), userId = any())).thenReturn(true)

        assertThrows<IllegalArgumentException> { _service.create(dto = dto) }

        verify(_repository, never()).save(any())
    }

    @Test
    fun `should return all calendars`() {
        val calendar = Calendar(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢",
            userId = _userId
        )
        val calendars: List<Calendar> =
            listOf(calendar, calendar.copy(id = UUID.randomUUID()), calendar.copy(id = UUID.randomUUID()))

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findAllByUserId(userId = _userId)).thenReturn(calendars)

        val result: List<CalendarDto> = _service.getAll()

        assertEquals(calendars.size, result.size)
        assertEquals(result, calendars.map { it.toDto() })

        verify(_repository).findAllByUserId(userId = _userId)
    }

    @Test
    fun `should return calendar by id`() {
        val id: UUID = UUID.randomUUID()
        val calendar = Calendar(
            id = id,
            name = "Test",
            emoji = "🟢",
            userId = _userId
        )

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.of(calendar))

        val result: CalendarDto = _service.getById(id = id)

        assertEquals(calendar.toDto(), result)

        verify(_repository).findByIdAndUserId(id = id, userId = _userId)
    }

    @Test
    fun `should throw error when calendar id not found`() {
        val id: UUID = UUID.randomUUID()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.getById(id = id) }

        verify(_repository).findByIdAndUserId(id = id, userId = _userId)
    }

    @Test
    fun `should return list of filtered calendars`() {
        val calendar = Calendar(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢",
            userId = _userId
        )
        val filter = CalendarFilterDto(
            name = "Test",
            emoji = null
        )
        val calendars: List<Calendar> = listOf(calendar)

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.filter(userId = _userId, name = filter.name, emoji = filter.emoji)).thenReturn(calendars)

        val result: List<CalendarDto> = _service.filter(filter = filter)

        assertEquals(result.size, calendars.size)
        assertEquals(result, calendars.map { it.toDto() })

        verify(_repository).filter(userId = _userId, name = filter.name, emoji = filter.emoji)
    }

    @Test
    fun `should return updated calendar with old title`() {
        val id = UUID.randomUUID()
        val existing = Calendar(
            id = id,
            name = "Test",
            emoji = "🟢",
            userId = _userId
        )
        val dto = existing.copy(emoji = "🔴").toDto()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.of(existing))
        whenever(_repository.save(any<Calendar>())).thenAnswer { it.arguments[0] as Calendar }

        val result = _service.update(id = id, dto = dto)

        assertEquals(dto, result)

        verify(_repository, never()).existsByNameAndUserId(any(), any())
        verify(_repository).save(any())
    }

    @Test
    fun `should return updated calendar with new title`() {
        val id = UUID.randomUUID()
        val existing = Calendar(
            id = id,
            name = "Test",
            emoji = "🟢",
            userId = _userId
        )
        val dto = existing.copy(name = "Test2").toDto()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.of(existing))
        whenever(_repository.existsByNameAndUserId(name = dto.name, userId = _userId)).thenReturn(false)
        whenever(_repository.save(any<Calendar>())).thenAnswer { it.arguments[0] as Calendar }

        val result = _service.update(id = id, dto = dto)

        assertEquals(dto, result)

        verify(_repository).existsByNameAndUserId(name = dto.name, userId = _userId)
        verify(_repository).save(any())
    }

    @Test
    fun `should throw error when updating to duplicate title`() {
        val id = UUID.randomUUID()
        val existing = Calendar(
            id = id,
            name = "Test",
            emoji = "🟢",
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
    fun `should delete calendar when exists`() {
        val id: UUID = UUID.randomUUID()
        val existing = Calendar(
            id = id,
            name = "Test",
            emoji = "🟢",
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
    fun `should throw error when deleting non existing calendar`() {
        val id: UUID = UUID.randomUUID()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = id, userId = _userId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.delete(id = id) }

        verify(_repository).findByIdAndUserId(id = id, userId = _userId)
        verify(_repository, never()).delete(any<Calendar>())
    }

}
