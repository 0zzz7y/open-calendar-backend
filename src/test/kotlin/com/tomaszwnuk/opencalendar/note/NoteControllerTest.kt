package com.tomaszwnuk.opencalendar.note

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryColorHelper
import com.tomaszwnuk.opencalendar.domain.note.Note
import com.tomaszwnuk.opencalendar.domain.note.NoteController
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteFilterDto
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
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.awt.Color
import java.util.UUID

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NoteControllerTest {

    @Mock
    private lateinit var _noteService: NoteService

    @InjectMocks
    private lateinit var _noteController: NoteController

    private lateinit var _sampleNote: Note

    private lateinit var _sampleNoteDto: NoteDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        val sampleCalendar = Calendar(
            title = "Personal",
            emoji = "\uD83C\uDFE0"
        )
        val sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Shopping",
            color = CategoryColorHelper.toHex(Color.YELLOW)
        )
        _sampleNote = Note(
            id = UUID.randomUUID(),
            title = "Groceries",
            description = "Buy milk, eggs, and bread",
            calendar = sampleCalendar,
            category = sampleCategory
        )
        _sampleNoteDto = _sampleNote.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created note with status code 201 Created`() {
        whenever(_noteService.create(_sampleNoteDto)).thenReturn(_sampleNote)

        val response: ResponseEntity<NoteDto> = _noteController.create(_sampleNoteDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(_sampleNote.id, response.body?.id)
        assertEquals(_sampleNote.title, response.body?.title)
        assertEquals(_sampleNote.description, response.body?.description)

        verify(_noteService).create(_sampleNoteDto)
    }

    @Test
    fun `should return paginated list of all notes with status code 200 OK`() {
        val notes: List<Note> = listOf(_sampleNote, _sampleNote.copy(), _sampleNote.copy())
        whenever(_noteService.getAll(_pageable)).thenReturn(PageImpl(notes))

        val response: ResponseEntity<Page<NoteDto>> = _noteController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size.toLong(), response.body?.totalElements)
        assertEquals(notes.map { it.id }, response.body?.content?.map { it.id })
        assertEquals(notes.map { it.title }, response.body?.content?.map { it.title })

        verify(_noteService).getAll(_pageable)
    }

    @Test
    fun `should return note by id with status code 200 OK`() {
        val id: UUID = _sampleNote.id
        whenever(_noteService.getById(id)).thenReturn(_sampleNote)

        val response: ResponseEntity<NoteDto> = _noteController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(_sampleNote.id, response.body?.id)
        assertEquals(_sampleNote.title, response.body?.title)
        assertEquals(_sampleNote.description, response.body?.description)

        verify(_noteService).getById(id)
    }

    @Test
    fun `should return paginated list of filtered notes with status code 200 OK`() {
        val filter = NoteFilterDto(title = "Groceries")
        val notes: List<Note> = listOf(_sampleNote, _sampleNote.copy(), _sampleNote.copy())

        whenever(_noteService.filter(eq(filter), eq(_pageable))).thenReturn(PageImpl(notes))

        val response: ResponseEntity<Page<NoteDto>> = _noteController.filter(
            filter.title,
            null,
            null,
            null,
            _pageable
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size.toLong(), response.body?.totalElements)
        assertEquals(notes.map { it.title }, response.body?.content?.map { it.title })

        verify(_noteService).filter(eq(filter), eq(_pageable))
    }

    @Test
    fun `should return updated note with status code 200 OK`() {
        val updatedNote: Note = _sampleNote.copy(title = "Updated Note")
        whenever(_noteService.update(_sampleNote.id, _sampleNoteDto)).thenReturn(updatedNote)

        val response: ResponseEntity<NoteDto> = _noteController.update(_sampleNote.id, _sampleNoteDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(updatedNote.id, response.body?.id)
        assertEquals("Updated Note", response.body?.title)
        assertEquals(updatedNote.description, response.body?.description)

        verify(_noteService).update(_sampleNote.id, _sampleNoteDto)
    }

    @Test
    fun `should delete note with status code 204 No Content`() {
        doNothing().whenever(_noteService).delete(_sampleNote.id)

        val response: ResponseEntity<Void> = _noteController.delete(_sampleNote.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_noteService).delete(_sampleNote.id)
    }

}
