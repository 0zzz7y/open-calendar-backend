package com.tomaszwnuk.opencalendar.calendar

import com.tomaszwnuk.opencalendar.domain.calendar.*
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.user.UserService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class CalendarServiceTest {

    @Mock
    private lateinit var _repository: CalendarRepository

    @Mock
    private lateinit var _userService: UserService

    private lateinit var _service: CalendarService

    private lateinit var _calendar: Calendar

    private lateinit var _calendarList: List<Calendar>

    private lateinit var _userId: UUID

    @BeforeEach
    fun setUp() {
        _userId = UUID.randomUUID()

        _service = CalendarService(
            _calendarRepository = _repository,
            _userService = _userService
        )

        _calendar = Calendar(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢",
            userId = _userId
        )

        _calendarList = listOf(_calendar, _calendar.copy(), _calendar.copy())
    }

    @Test
    fun `should return created calendar`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.existsByNameAndUserId(name = any(), userId = any())).thenReturn(false)
        whenever(_repository.save(any<Calendar>())).thenReturn(_calendar)

        val result: CalendarDto = _service.create(dto = _calendar.toDto())

        assertEquals(_calendar.toDto(), result)

        verify(_repository).save(any())
    }

    @Test
    fun `should throw error when creating calendar with duplicate title`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.existsByNameAndUserId(name = any(), userId = any())).thenReturn(true)

        assertThrows<IllegalArgumentException> { _service.create(dto = _calendar.toDto()) }

        verify(_repository, never()).save(any())
    }

    @Test
    fun `should return calendar by id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = _calendar.id, userId = _userId)).thenReturn(Optional.of(_calendar))

        val result: CalendarDto = _service.getById(id = _calendar.id)

        assertEquals(_calendar.toDto(), result)

        verify(_repository).findByIdAndUserId(id = _calendar.id, userId = _userId)
    }

    @Test
    fun `should throw error when calendar id not found`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = _calendar.id, userId = _userId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.getById(id = _calendar.id) }

        verify(_repository).findByIdAndUserId(id = _calendar.id, userId = _userId)
    }

    @Test
    fun `should return all calendars`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findAllByUserId(userId = _userId)).thenReturn(_calendarList)

        val result: List<CalendarDto> = _service.getAll()

        assertEquals(_calendarList.size, result.size)
        assertEquals(result, _calendarList.map { it.toDto() })

        verify(_repository).findAllByUserId(userId = _userId)
    }

    @Test
    fun `should return list of filtered calendars`() {
        val filter = CalendarFilterDto(
            name = "Test",
            emoji = null
        )

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.filter(userId = _userId, name = filter.name, emoji = filter.emoji))
            .thenReturn(_calendarList)

        val result: List<CalendarDto> = _service.filter(filter = filter)

        assertEquals(result.size, _calendarList.size)
        assertEquals(result, _calendarList.map { it.toDto() })

        verify(_repository).filter(userId = _userId, name = filter.name, emoji = filter.emoji)
    }

    @Test
    fun `should return updated calendar with old title`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = _calendar.id, userId = _userId)).thenReturn(Optional.of(_calendar))
        whenever(_repository.save(any<Calendar>())).thenAnswer { it.arguments[0] as Calendar }

        val result = _service.update(id = _calendar.id, dto = _calendar.toDto())

        assertEquals(_calendar.toDto(), result)

        verify(_repository, never()).existsByNameAndUserId(any(), any())
        verify(_repository).save(any())
    }

    @Test
    fun `should return updated calendar with new title`() {
        val dto: CalendarDto = _calendar.copy(name = "Test2").toDto()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = _calendar.id, userId = _userId)).thenReturn(Optional.of(_calendar))
        whenever(_repository.existsByNameAndUserId(name = dto.name, userId = _userId)).thenReturn(false)
        whenever(_repository.save(any<Calendar>())).thenAnswer { it.arguments[0] as Calendar }

        val result = _service.update(id = dto.id!!, dto = dto)

        assertEquals(dto, result)

        verify(_repository).existsByNameAndUserId(name = dto.name, userId = _userId)
        verify(_repository).save(any())
    }

    @Test
    fun `should throw error when updating to duplicate title`() {
        val dto: CalendarDto = _calendar.copy(name = "Test2").toDto()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = _calendar.id, userId = _userId)).thenReturn(Optional.of(_calendar))
        whenever(_repository.existsByNameAndUserId(name = dto.name, userId = _userId)).thenReturn(true)

        assertThrows<IllegalArgumentException> { _service.update(id = dto.id!!, dto = dto) }

        verify(_repository).existsByNameAndUserId(name = dto.name, userId = _userId)
        verify(_repository, never()).save(any())
    }

    @Test
    fun `should delete calendar when exists`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = _calendar.id, userId = _userId)).thenReturn(Optional.of(_calendar))
        doNothing().whenever(_repository).delete(_calendar)

        _service.delete(id = _calendar.id)

        verify(_repository).findByIdAndUserId(id = _calendar.id, userId = _userId)
        verify(_repository).delete(_calendar)
    }

    @Test
    fun `should throw error when deleting non existing calendar`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_repository.findByIdAndUserId(id = _calendar.id, userId = _userId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.delete(id = _calendar.id) }

        verify(_repository).findByIdAndUserId(id = _calendar.id, userId = _userId)
        verify(_repository, never()).delete(any<Calendar>())
    }

}
