/*
 * Copyright (c) Tomasz Wnuk
 */

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
        val dto = CalendarDto(id = null, name = "Calendar", emoji = "🟢")
        val savedId = UUID.randomUUID()

        whenever(_repository.existsByName("Calendar")).thenReturn(false)
        whenever(_repository.save(any<Calendar>())).thenAnswer { invocation ->
            val arg = invocation.getArgument<Calendar>(0)
            Calendar(id = savedId, name = arg.name, emoji = arg.emoji)
        }

        val result = _service.create(dto = dto)

        assertNotNull(result.id)
        assertEquals(savedId, result.id)
        assertEquals("Calendar", result.name)
        assertEquals("🟢", result.emoji)

        verify(_repository).existsByName("Calendar")
        verify(_repository).save(argThat { name == "Calendar" && emoji == "🟢" })
    }

    @Test
    fun `should throw error when creating calendar with duplicate title`() {
        val dto = CalendarDto(id = null, name = "Duplicate Title", emoji = "🟢")

        whenever(_repository.existsByName(name = "Duplicate Title")).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            _service.create(dto = dto)
        }

        verify(_repository).existsByName(name = "Duplicate Title")
        verify(_repository, never()).save(any<Calendar>())
    }

    @Test
    fun `should return all calendars`() {
        val calendar1 = Calendar(id = UUID.randomUUID(), name = "Work Calendar", emoji = "🟢")
        val calendar2 = Calendar(id = UUID.randomUUID(), name = "Personal Calendar", emoji = "🔵")

        whenever(_repository.findAll()).thenReturn(listOf(calendar1, calendar2))

        val result = _service.getAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Work Calendar" && it.emoji == "🟢" })
        assertTrue(result.any { it.name == "Personal Calendar" && it.emoji == "🔵" })

        verify(_repository).findAll()
    }

    @Test
    fun `should return calendar by id`() {
        val id = UUID.randomUUID()
        val calendar = Calendar(id = id, name = "Team Calendar", emoji = "🟢")

        whenever(_repository.findById(id)).thenReturn(Optional.of(calendar))

        val result = _service.getById(id = id)

        assertEquals(id, result.id)
        assertEquals("Team Calendar", result.name)
        assertEquals("🟢", result.emoji)

        verify(_repository).findById(id)
    }

    @Test
    fun `should throw error when calendar id not found`() {
        val id = UUID.randomUUID()

        whenever(_repository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.getById(id = id)
        }

        verify(_repository).findById(id)
    }

    @Test
    fun `should return list of filtered calendars`() {
        val filter = CalendarFilterDto(name = "Project Calendar", emoji = "🟢")
        val matching = Calendar(id = UUID.randomUUID(), name = "Project Calendar", emoji = "🟢")

        whenever(_repository.filter(name = "Project Calendar", emoji = "🟢")).thenReturn(listOf(matching))

        val result = _service.filter(filter = filter)

        assertEquals(1, result.size)
        assertEquals("Project Calendar", result[0].name)
        assertEquals("🟢", result[0].emoji)

        verify(_repository).filter("Project Calendar", "🟢")
    }

    @Test
    fun `should return updated calendar with old title`() {
        val id = UUID.randomUUID()
        val existingCalendar = Calendar(id = id, name = "Old Title", emoji = "🟢")
        val dto = CalendarDto(id = id, name = "Old Title", emoji = "🔴")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existingCalendar))
        whenever(_repository.save(any<Calendar>())).thenAnswer { it.getArgument<Calendar>(0) }

        val result = _service.update(id = id, dto = dto)

        assertEquals("Old Title", result.name)
        assertEquals("🔴", result.emoji)

        verify(_repository).findById(id)
        verify(_repository, never()).existsByName(any())
        verify(_repository).save(argThat { emoji == "🔴" })
    }

    @Test
    fun `should return updated calendar with new title`() {
        val id = UUID.randomUUID()
        val existingCalendar = Calendar(id = id, name = "Old Title", emoji = "🟢")
        val dto = CalendarDto(id = id, name = "New Title", emoji = "🔴")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existingCalendar))
        whenever(_repository.existsByName("New Title")).thenReturn(false)
        whenever(_repository.save(any<Calendar>())).thenAnswer { it.getArgument<Calendar>(0) }

        val result = _service.update(id, dto)

        assertEquals("New Title", result.name)
        assertEquals("🔴", result.emoji)

        verify(_repository).findById(id)
        verify(_repository).existsByName(name = "New Title")
        verify(_repository).save(argThat { name == "New Title" && emoji == "🔴" })
    }

    @Test
    fun `should throw error when updating to duplicate title`() {
        val id = UUID.randomUUID()
        val existingCalendar = Calendar(id = id, name = "Old Title", emoji = "🟢")
        val dto = CalendarDto(id = id, name = "Duplicate Title", emoji = "🔴")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existingCalendar))
        whenever(_repository.existsByName(name = "Duplicate Title")).thenReturn(true)

        assertThrows<IllegalArgumentException> {
            _service.update(id, dto)
        }

        verify(_repository).findById(id)
        verify(_repository).existsByName(name = "Duplicate Title")
        verify(_repository, never()).save(any<Calendar>())
    }

    @Test
    fun `should delete calendar when exists`() {
        val id = UUID.randomUUID()
        val existingCalendar = Calendar(id = id, name = "ToDelete Calendar", emoji = "🟢")

        whenever(_repository.findById(id)).thenReturn(Optional.of(existingCalendar))
        doNothing().whenever(_repository).delete(existingCalendar)

        _service.delete(id = id)

        verify(_repository).findById(id)
        verify(_repository).delete(existingCalendar)
    }

    @Test
    fun `should throw error when deleting non existing calendar`() {
        val id = UUID.randomUUID()

        whenever(_repository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.delete(id = id)
        }

        verify(_repository).findById(id)
        verify(_repository, never()).delete(any<Calendar>())
    }

}
