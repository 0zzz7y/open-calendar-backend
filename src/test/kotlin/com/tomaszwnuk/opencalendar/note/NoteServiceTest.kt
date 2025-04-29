package com.tomaszwnuk.opencalendar.note

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryColorHelper
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.note.Note
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteFilterDto
import com.tomaszwnuk.opencalendar.domain.note.NoteRepository
import com.tomaszwnuk.opencalendar.domain.note.NoteService
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
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NoteServiceTest {

    @Mock
    private lateinit var _noteRepository: NoteRepository

    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var _noteService: NoteService

    private lateinit var _sampleCalendar: Calendar

    private lateinit var _sampleCategory: Category

    private lateinit var _sampleNote: Note

    private lateinit var _sampleNoteDto: NoteDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\uD83C\uDFE0"
        )
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Shopping",
            color = CategoryColorHelper.toHex(Color.YELLOW)
        )
        _sampleNote = Note(
            id = UUID.randomUUID(),
            title = "Groceries",
            description = "Buy milk, eggs, and bread",
            calendar = _sampleCalendar,
            category = _sampleCategory
        )
        _sampleNoteDto = _sampleNote.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created note`() {
        whenever(_calendarRepository.findById(_sampleNoteDto.calendarId)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleNoteDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(_sampleNote).whenever(_noteRepository).save(any())

        val result: Note = _noteService.create(_sampleNoteDto)

        assertNotNull(result)
        assertEquals(_sampleNote.id, result.id)
        assertEquals(_sampleNote.title, result.title)
        assertEquals(_sampleNote.description, result.description)

        verify(_noteRepository).save(any())
    }

    @Test
    fun `should return paginated list of all notes`() {
        val notes: List<Note> = listOf(_sampleNote, _sampleNote.copy(), _sampleNote.copy())
        whenever(_noteRepository.findAll(_pageable)).thenReturn(PageImpl(notes))

        val result: Page<Note> = _noteService.getAll(_pageable)

        assertEquals(notes.size, result.totalElements.toInt())
        assertEquals(notes.map { it.id }, result.content.map { it.id })
        assertEquals(notes.map { it.title }, result.content.map { it.title })

        verify(_noteRepository).findAll(_pageable)
    }

    @Test
    fun `should return note by id`() {
        val id: UUID = _sampleNote.id
        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(_sampleNote))

        val result: Note = _noteService.getById(id)

        assertNotNull(result)
        assertEquals(_sampleNote.id, result.id)
        assertEquals(_sampleNote.title, result.title)
        assertEquals(_sampleNote.description, result.description)

        verify(_noteRepository).findById(id)
    }

    @Test
    fun `should return paginated list of filtered notes`() {
        val filter = NoteFilterDto(title = "Groceries")
        val notes: List<Note> = listOf(_sampleNote, _sampleNote.copy(), _sampleNote.copy())

        whenever(
            _noteRepository.filter(
                eq(filter.title),
                isNull(),
                isNull(),
                isNull(),
                eq(_pageable)
            )
        ).thenReturn(PageImpl(notes))

        val result: Page<Note> = _noteService.filter(filter, _pageable)

        assertEquals(notes.size, result.totalElements.toInt())
        assertEquals(notes.map { it.title }, result.content.map { it.title })

        verify(_noteRepository).filter(
            eq(filter.title),
            isNull(),
            isNull(),
            isNull(),
            eq(_pageable)
        )
    }

    @Test
    fun `should return updated note`() {
        val id: UUID = _sampleNote.id
        val updatedNote: Note = _sampleNote.copy(title = "Updated note")

        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(_sampleNote))
        whenever(_calendarRepository.findById(_sampleNoteDto.calendarId)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleNoteDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(updatedNote).whenever(_noteRepository).save(any())

        val result: Note = _noteService.update(id, _sampleNoteDto)

        assertNotNull(result)
        assertEquals(updatedNote.id, result.id)
        assertEquals("Updated note", result.title)
        assertEquals(updatedNote.description, result.description)

        verify(_noteRepository).save(any())
    }

    @Test
    fun `should delete note by id`() {
        val id: UUID = _sampleNote.id
        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(_sampleNote))
        doNothing().whenever(_noteRepository).delete(_sampleNote)

        _noteService.delete(id)

        verify(_noteRepository).delete(_sampleNote)
    }

}
