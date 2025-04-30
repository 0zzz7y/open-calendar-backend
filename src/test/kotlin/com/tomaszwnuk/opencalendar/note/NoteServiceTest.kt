/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.note

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.note.*
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
 * Unit tests for the `NoteService` class.
 * Verifies the behavior of the service methods using mocked dependencies.
 */
@ExtendWith(MockitoExtension::class)
internal class NoteServiceTest {

    /**
     * Mocked instance of `NoteRepository` for simulating note-related database operations.
     */
    @Mock
    private lateinit var _noteRepository: NoteRepository

    /**
     * Mocked instance of `CalendarRepository` for simulating calendar-related database operations.
     */
    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    /**
     * Mocked instance of `CategoryRepository` for simulating category-related database operations.
     */
    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    /**
     * Instance of `NoteService` under test.
     */
    private lateinit var _service: NoteService

    /**
     * Sample `Calendar` instance used in tests.
     */
    private val sampleCalendar = Calendar(
        id = UUID.randomUUID(), title = "Project Calendar", emoji = "📅"
    )

    /**
     * Sample `Category` instance used in tests.
     */
    private val sampleCategory = Category(
        id = UUID.randomUUID(), title = "Announcements", color = "#FFA500"
    )

    /**
     * Sets up the test environment before each test.
     * Initializes the `NoteService` with mocked repositories.
     */
    @BeforeEach
    fun setUp() {
        _service = NoteService(_noteRepository, _calendarRepository, _categoryRepository)
    }

    /**
     * Tests the creation of a note.
     * Verifies that the service returns the created note with a generated ID.
     */
    @Test
    fun `should return created note`() {
        val dto = NoteDto(
            title = "Weekly Update",
            description = "Team progress overview",
            calendarId = sampleCalendar.id,
            categoryId = sampleCategory.id
        )
        val savedId = UUID.randomUUID()

        whenever(_calendarRepository.findById(sampleCalendar.id)).thenReturn(Optional.of(sampleCalendar))
        whenever(_categoryRepository.findById(sampleCategory.id)).thenReturn(Optional.of(sampleCategory))
        whenever(_noteRepository.save(any<Note>())).thenAnswer { invocation ->
            val arg = invocation.getArgument<Note>(0)
            arg.copy(id = savedId)
        }

        val result = _service.create(dto)

        assertNotNull(result.id)
        assertEquals(savedId, result.id)
        assertEquals("Weekly Update", result.title)
        assertEquals("Team progress overview", result.description)
        assertEquals(sampleCalendar.id, result.calendarId)
        assertEquals(sampleCategory.id, result.categoryId)

        verify(_calendarRepository).findById(sampleCalendar.id)
        verify(_categoryRepository).findById(sampleCategory.id)
        verify(_noteRepository).save(argThat { title == "Weekly Update" && description == "Team progress overview" })
    }

    /**
     * Tests the creation of a note with a missing calendar.
     * Verifies that the service throws a `NoSuchElementException`.
     */
    @Test
    fun `should throw error when creating note with missing calendar`() {
        val dto = NoteDto(
            title = "Missing Calendar",
            description = "No calendar available",
            calendarId = UUID.randomUUID(),
            categoryId = null
        )
        whenever(_calendarRepository.findById(dto.calendarId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.create(dto)
        }

        verify(_calendarRepository).findById(dto.calendarId)
        verify(_noteRepository, never()).save(any<Note>())
    }

    /**
     * Tests retrieving a note by its ID.
     * Verifies that the service returns the correct note.
     */
    @Test
    fun `should return note by id`() {
        val id = UUID.randomUUID()
        val note = Note(
            id = id,
            title = "Memo",
            description = "Keep this in mind",
            calendar = sampleCalendar,
            category = null
        )
        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(note))

        val result = _service.getById(id)

        assertEquals(id, result.id)
        assertEquals("Memo", result.title)
        assertEquals("Keep this in mind", result.description)

        verify(_noteRepository).findById(id)
    }

    /**
     * Tests retrieving a note by a non-existent ID.
     * Verifies that the service throws a `NoSuchElementException`.
     */
    @Test
    fun `should throw error when note id not found`() {
        val id = UUID.randomUUID()
        whenever(_noteRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.getById(id)
        }

        verify(_noteRepository).findById(id)
    }

    /**
     * Tests retrieving all notes by calendar ID.
     * Verifies that the service returns a list of notes associated with the specified calendar.
     */
    @Test
    fun `should return all notes by calendar id`() {
        val note1 = Note(
            title = "Standup Notes", description = "Daily standup summary",
            calendar = sampleCalendar
        )
        val note2 = Note(
            title = "Planning Notes", description = "Sprint planning details",
            calendar = sampleCalendar
        )
        whenever(_noteRepository.findAllByCalendarId(sampleCalendar.id)).thenReturn(listOf(note1, note2))

        val result = _service.getAllByCalendarId(sampleCalendar.id)

        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "Standup Notes" })
        assertTrue(result.any { it.title == "Planning Notes" })

        verify(_noteRepository).findAllByCalendarId(sampleCalendar.id)
    }

    /**
     * Tests retrieving all notes by category ID.
     * Verifies that the service returns a list of notes associated with the specified category.
     */
    @Test
    fun `should return all notes by category id`() {
        val note = Note(
            title = "Announcement", description = "New policy",
            calendar = sampleCalendar,
            category = sampleCategory
        )
        whenever(_noteRepository.findAllByCategoryId(sampleCategory.id)).thenReturn(listOf(note))

        val result = _service.getAllByCategoryId(sampleCategory.id)

        assertEquals(1, result.size)
        assertEquals("Announcement", result[0].title)

        verify(_noteRepository).findAllByCategoryId(sampleCategory.id)
    }

    /**
     * Tests retrieving all notes.
     * Verifies that the service returns a list of all notes.
     */
    @Test
    fun `should return all notes`() {
        val n1 = Note(
            title = "Note One", description = "Desc one",
            calendar = sampleCalendar
        )
        val n2 = Note(
            title = "Note Two", description = "Desc two",
            calendar = sampleCalendar
        )
        whenever(_noteRepository.findAll()).thenReturn(listOf(n1, n2))

        val result = _service.getAll()

        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "Note One" })
        assertTrue(result.any { it.title == "Note Two" })

        verify(_noteRepository).findAll()
    }

    /**
     * Tests filtering notes based on criteria.
     * Verifies that the service returns a list of matching notes.
     */
    @Test
    fun `should return filtered notes`() {
        val filter = NoteFilterDto(
            title = "Standup", description = null,
            calendarId = null, categoryId = null
        )
        val standupNote = Note(
            title = "Standup Notes", description = "Morning updates",
            calendar = sampleCalendar
        )
        whenever(_noteRepository.filter("Standup", null, null, null)).thenReturn(listOf(standupNote))

        val result = _service.filter(filter)

        assertEquals(1, result.size)
        assertEquals("Standup Notes", result[0].title)

        verify(_noteRepository).filter("Standup", null, null, null)
    }

    /**
     * Tests updating a note.
     * Verifies that the service updates the note and returns the updated entity.
     */
    @Test
    fun `should return updated note`() {
        val id = UUID.randomUUID()
        val existing = Note(
            id = id,
            title = "Draft",
            description = "Initial draft",
            calendar = sampleCalendar,
            category = sampleCategory
        )
        val dto = existing.toDto().copy(title = "Final Draft", description = "Updated draft")
        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(existing))
        whenever(_calendarRepository.findById(dto.calendarId)).thenReturn(Optional.of(sampleCalendar))
        whenever(_categoryRepository.findById(dto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
        whenever(_noteRepository.save(any<Note>())).thenAnswer { it.getArgument<Note>(0) }

        val result = _service.update(id, dto)

        assertEquals("Final Draft", result.title)
        assertEquals("Updated draft", result.description)
        verify(_noteRepository).findById(id)
        verify(_noteRepository).save(argThat { title == "Final Draft" && description == "Updated draft" })
    }

    /**
     * Tests updating a non-existent note.
     * Verifies that the service throws a `NoSuchElementException`.
     */
    @Test
    fun `should throw error when updating non existing note`() {
        val id = UUID.randomUUID()
        val dto = NoteDto(
            id = null,
            title = "Ghost Note",
            description = "Does not exist",
            calendarId = sampleCalendar.id,
            categoryId = null
        )
        whenever(_noteRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> {
            _service.update(id, dto)
        }

        verify(_noteRepository).findById(id)
    }

    /**
     * Tests deleting a note that exists.
     * Verifies that the service deletes the note.
     */
    @Test
    fun `should delete note when exists`() {
        val id = UUID.randomUUID()
        val existing = Note(
            title = "Reminder",
            description = "Pay bills",
            calendar = sampleCalendar
        )
        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(existing))
        doNothing().whenever(_noteRepository).delete(existing)

        _service.delete(id)

        verify(_noteRepository).findById(id)
        verify(_noteRepository).delete(existing)
    }

    /**
     * Tests deleting all notes by calendar ID.
     * Verifies that the service deletes all notes associated with the specified calendar.
     */
    @Test
    fun `should delete all notes by calendar id`() {
        val calId = sampleCalendar.id
        val noteA = Note(
            title = "Note A", description = "Desc A", calendar = sampleCalendar
        )
        val noteB = noteA.copy(id = UUID.randomUUID())
        whenever(_noteRepository.findAllByCalendarId(calId)).thenReturn(listOf(noteA, noteB))
        doNothing().whenever(_noteRepository).deleteAll(listOf(noteA, noteB))

        _service.deleteByCalendarId(calId)

        verify(_noteRepository).findAllByCalendarId(calId)
        verify(_noteRepository).deleteAll(listOf(noteA, noteB))
    }

    /**
     * Tests clearing the category for all notes by category ID.
     * Verifies that the service removes the category association from all notes in the specified category.
     */
    @Test
    fun `should clear category for all notes by category id`() {
        val catId = sampleCategory.id
        val note1 = Note(
            title = "Cat Note 1", description = "Desc1",
            calendar = sampleCalendar, category = sampleCategory
        )
        val note2 = note1.copy(id = UUID.randomUUID())
        whenever(_noteRepository.findAllByCategoryId(catId)).thenReturn(listOf(note1, note2))
        whenever(_noteRepository.save(any<Note>())).thenAnswer { it.getArgument<Note>(0) }

        _service.deleteCategoryByCategoryId(catId)

        verify(_noteRepository).findAllByCategoryId(catId)
        verify(_noteRepository).save(argThat { id == note1.id && category == null })
        verify(_noteRepository).save(argThat { id == note2.id && category == null })
    }

}
