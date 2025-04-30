package com.tomaszwnuk.opencalendar.calendar

import com.tomaszwnuk.opencalendar.domain.calendar.*
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
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
internal class CalendarServiceTest {

    @Mock
    private lateinit var _repository: CalendarRepository

    private lateinit var _service: CalendarService

    @BeforeEach
    fun setUp() {
        _service = CalendarService(_repository)
    }

    @Test
    fun `should return created calendar`() {
        val dto = CalendarDto(id = null, title = "Calendar", emoji = "🟢")
        val savedId = UUID.randomUUID()

        whenever(_repository.existsByTitle("Calendar")).thenReturn(false)
        whenever(_repository.save(any<Calendar>())).thenAnswer { invocation ->
            val arg = invocation.getArgument<Calendar>(0)
            Calendar(id = savedId, title = arg.title, emoji = arg.emoji)
        }

        val result = _service.create(dto)

        assertNotNull(result.id)
        assertEquals(savedId, result.id)
        assertEquals("Calendar", result.title)
        assertEquals("🟢", result.emoji)

        verify(_repository).existsByTitle("Calendar")
        verify(_repository).save(argThat { title == "Calendar" && emoji == "🟢" })
    }

    @Test
    fun `should throw error when creating calendar with duplicate title`() {
        val dto = CalendarDto(id = null, title = "Duplicate Title", emoji = "🟢")

        whenever(_repository.existsByTitle("Duplicate Title")).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            _service.create(dto)
        }

        verify(_repository).existsByTitle("Duplicate Title")
        verify(_repository, never()).save(any<Calendar>())
    }

    @Test
    fun `should return all calendars`() {
        val calendar1 = Calendar(id = UUID.randomUUID(), title = "Work Calendar", emoji = "🟢")
        val calendar2 = Calendar(id = UUID.randomUUID(), title = "Personal Calendar", emoji = "🔵")

        whenever(_repository.findAll()).thenReturn(listOf(calendar1, calendar2))

        val result = _service.getAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "Work Calendar" && it.emoji == "🟢" })
        assertTrue(result.any { it.title == "Personal Calendar" && it.emoji == "🔵" })

        verify(_repository).findAll()
    }

    @Test
    fun `should return calendar by id`() {
        val id = UUID.randomUUID()
        val calendar = Calendar(id = id, title = "Team Calendar", emoji = "🟢")

        whenever(_repository.findById(id)).thenReturn(Optional.of(calendar))

        val result = _service.getById(id)

        assertEquals(id, result.id)
        assertEquals("Team Calendar", result.title)
        assertEquals("🟢", result.emoji)

        verify(_repository).findById(id)
    }

    @Test
    fun `should throw error when calendar id not found`() {
        val id = UUID.randomUUID()

        whenever(_repository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.getById(id)
        }

        verify(_repository).findById(id)
    }

    @Test
    fun `should return list of filtered calendars`() {
        val filter = CalendarFilterDto(title = "Project Calendar", emoji = "🟢")
        val matching = Calendar(id = UUID.randomUUID(), title = "Project Calendar", emoji = "🟢")

        whenever(_repository.filter("Project Calendar", "🟢")).thenReturn(listOf(matching))

        val result = _service.filter(filter)

        assertEquals(1, result.size)
        assertEquals("Project Calendar", result[0].title)
        assertEquals("🟢", result[0].emoji)

        verify(_repository).filter("Project Calendar", "🟢")
    }

    @Test
    fun `should return updated calendar with old title`() {
        val id = UUID.randomUUID()
        val existingCalendar = Calendar(id = id, title = "Old Title", emoji = "🟢")
        val dto = CalendarDto(id = id, title = "Old Title", emoji = "🔴")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existingCalendar))
        whenever(_repository.save(any<Calendar>())).thenAnswer { it.getArgument<Calendar>(0) }

        val result = _service.update(id, dto)

        assertEquals("Old Title", result.title)
        assertEquals("🔴", result.emoji)

        verify(_repository).findById(id)
        verify(_repository, never()).existsByTitle(any())
        verify(_repository).save(argThat { emoji == "🔴" })
    }

    @Test
    fun `should return updated calendar with new title`() {
        val id = UUID.randomUUID()
        val existingCalendar = Calendar(id = id, title = "Old Title", emoji = "🟢")
        val dto = CalendarDto(id = id, title = "New Title", emoji = "🔴")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existingCalendar))
        whenever(_repository.existsByTitle("New Title")).thenReturn(false)
        whenever(_repository.save(any<Calendar>())).thenAnswer { it.getArgument<Calendar>(0) }

        val result = _service.update(id, dto)

        assertEquals("New Title", result.title)
        assertEquals("🔴", result.emoji)

        verify(_repository).findById(id)
        verify(_repository).existsByTitle("New Title")
        verify(_repository).save(argThat { title == "New Title" && emoji == "🔴" })
    }

    @Test
    fun `should throw error when updating to duplicate title`() {
        val id = UUID.randomUUID()
        val existingCalendar = Calendar(id = id, title = "Old Title", emoji = "🟢")
        val dto = CalendarDto(id = id, title = "Duplicate Title", emoji = "🔴")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existingCalendar))
        whenever(_repository.existsByTitle("Duplicate Title")).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            _service.update(id, dto)
        }

        verify(_repository).findById(id)
        verify(_repository).existsByTitle("Duplicate Title")
        verify(_repository, never()).save(any<Calendar>())
    }

    @Test
    fun `should delete calendar when exists`() {
        val id = UUID.randomUUID()
        val existingCalendar = Calendar(id = id, title = "ToDelete Calendar", emoji = "🟢")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existingCalendar))
        doNothing().whenever(_repository).delete(existingCalendar)

        _service.delete(id)

        verify(_repository).findById(id)
        verify(_repository).delete(existingCalendar)
    }

    @Test
    fun `should throw error when deleting non existing calendar`() {
        val id = UUID.randomUUID()

        whenever(_repository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.delete(id)
        }

        verify(_repository).findById(id)
        verify(_repository, never()).delete(any<Calendar>())
    }

}
