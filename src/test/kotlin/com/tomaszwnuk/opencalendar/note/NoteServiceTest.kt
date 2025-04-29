package com.tomaszwnuk.opencalendar.note

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.category.CategoryColorHelper
import com.tomaszwnuk.opencalendar.category.CategoryRepository
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
class NoteServiceTest {

    @Mock
    private lateinit var noteRepository: NoteRepository

    @Mock
    private lateinit var calendarRepository: CalendarRepository

    @Mock
    private lateinit var categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var noteService: NoteService

    private lateinit var sampleCalendar: Calendar

    private lateinit var sampleCategory: Category

    private lateinit var sampleNote: Note

    private lateinit var sampleNoteDto: NoteDto

    private lateinit var pageable: Pageable

    @BeforeEach
    fun setup() {
        sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\uD83C\uDFE0"
        )
        sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Shopping",
            color = CategoryColorHelper.toHex(Color.YELLOW)
        )
        sampleNote = Note(
            id = UUID.randomUUID(),
            title = "Groceries",
            description = "Buy milk, eggs, and bread",
            calendar = sampleCalendar,
            category = sampleCategory
        )
        sampleNoteDto = sampleNote.toDto()
        pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created note`() {
        whenever(calendarRepository.findById(sampleNoteDto.calendarId)).thenReturn(Optional.of(sampleCalendar))
        whenever(categoryRepository.findById(sampleNoteDto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
        doReturn(sampleNote).whenever(noteRepository).save(any())

        val result: Note = noteService.create(sampleNoteDto)

        assertNotNull(result)
        assertEquals(sampleNote.id, result.id)
        assertEquals(sampleNote.title, result.title)
        assertEquals(sampleNote.description, result.description)

        verify(noteRepository).save(any())
    }

    @Test
    fun `should return paginated list of all notes`() {
        val notes: List<Note> = listOf(sampleNote, sampleNote.copy(), sampleNote.copy())
        whenever(noteRepository.findAll(pageable)).thenReturn(PageImpl(notes))

        val result: Page<Note> = noteService.getAll(pageable)

        assertEquals(notes.size, result.totalElements.toInt())
        assertEquals(notes.map { it.id }, result.content.map { it.id })
        assertEquals(notes.map { it.title }, result.content.map { it.title })

        verify(noteRepository).findAll(pageable)
    }

    @Test
    fun `should return note by id`() {
        val id: UUID = sampleNote.id
        whenever(noteRepository.findById(id)).thenReturn(Optional.of(sampleNote))

        val result: Note = noteService.getById(id)

        assertNotNull(result)
        assertEquals(sampleNote.id, result.id)
        assertEquals(sampleNote.title, result.title)
        assertEquals(sampleNote.description, result.description)

        verify(noteRepository).findById(id)
    }

    @Test
    fun `should return paginated list of filtered notes`() {
        val filter = NoteFilterDto(title = "Groceries")
        val notes: List<Note> = listOf(sampleNote, sampleNote.copy(), sampleNote.copy())

        whenever(
            noteRepository.filter(
                eq(filter.title),
                isNull(),
                isNull(),
                isNull(),
                eq(pageable)
            )
        ).thenReturn(PageImpl(notes))

        val result: Page<Note> = noteService.filter(filter, pageable)

        assertEquals(notes.size, result.totalElements.toInt())
        assertEquals(notes.map { it.title }, result.content.map { it.title })

        verify(noteRepository).filter(
            eq(filter.title),
            isNull(),
            isNull(),
            isNull(),
            eq(pageable)
        )
    }

    @Test
    fun `should return updated note`() {
        val id: UUID = sampleNote.id
        val updatedNote: Note = sampleNote.copy(title = "Updated note")

        whenever(noteRepository.findById(id)).thenReturn(Optional.of(sampleNote))
        whenever(calendarRepository.findById(sampleNoteDto.calendarId)).thenReturn(Optional.of(sampleCalendar))
        whenever(categoryRepository.findById(sampleNoteDto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
        doReturn(updatedNote).whenever(noteRepository).save(any())

        val result: Note = noteService.update(id, sampleNoteDto)

        assertNotNull(result)
        assertEquals(updatedNote.id, result.id)
        assertEquals("Updated note", result.title)
        assertEquals(updatedNote.description, result.description)

        verify(noteRepository).save(any())
    }

    @Test
    fun `should delete note by id`() {
        val id: UUID = sampleNote.id
        whenever(noteRepository.findById(id)).thenReturn(Optional.of(sampleNote))
        doNothing().whenever(noteRepository).delete(sampleNote)

        noteService.delete(id)

        verify(noteRepository).delete(sampleNote)
    }

}
