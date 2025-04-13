package com.tomaszwnuk.dailyassistant.note

import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.category.Category
import org.junit.jupiter.api.Assertions.assertEquals
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NoteControllerTest {

    @Mock
    private lateinit var _noteService: NoteService

    @InjectMocks
    private lateinit var _noteController: NoteController

    private lateinit var _sampleNote: Note

    private lateinit var _sampleDto: NoteDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        val sampleCalendar = Calendar(name = "Personal")
        val sampleCategory = Category(name = "Shopping")
        _sampleNote = Note(
            id = UUID.randomUUID(),
            name = "Groceries",
            description = "Buy milk, eggs, and bread",
            calendar = sampleCalendar,
            category = sampleCategory
        )
        _sampleDto = _sampleNote.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created note with status code 201 Created`() {
        whenever(_noteService.create(any())).thenReturn(_sampleNote)
        val response: ResponseEntity<NoteDto> = _noteController.create(_sampleDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleDto, response.body)
        verify(_noteService).create(_sampleDto)
    }

    @Test
    fun `should return paginated list of notes with status code 200 OK`() {
        val notes: List<Note> = listOf(_sampleNote, _sampleNote, _sampleNote)

        whenever(_noteService.getAll(_pageable)).thenReturn(PageImpl(notes))
        val response: ResponseEntity<Page<NoteDto>> = _noteController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size, response.body?.totalElements?.toInt())
        verify(_noteService).getAll(_pageable)
    }

    @Test
    fun `should return note by id with status code 200 OK`() {
        val id: UUID = _sampleNote.id

        whenever(_noteService.getById(id)).thenReturn(_sampleNote)
        val response: ResponseEntity<NoteDto> = _noteController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(id, response.body?.id)
        verify(_noteService).getById(id)
    }

    @Test
    fun `should return filtered list of notes with status code 200 OK`() {
        val filter = NoteFilterDto(name = "Groceries")
        val notes: List<Note> = listOf(_sampleNote, _sampleNote, _sampleNote)

        whenever(_noteService.filter(eq(filter), eq(_pageable))).thenReturn(PageImpl(notes))
        val response: ResponseEntity<Page<NoteDto>> = _noteController.filter(
            eq(filter.name),
            null,
            null,
            null,
            eq(_pageable)
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size, response.body?.totalElements?.toInt())
        verify(_noteService).filter(eq(filter), eq(_pageable))
    }

    @Test
    fun `should return updated task with status code 200 OK`() {
        val updated: Note = _sampleNote.copy(name = "Updated Note")

        whenever(_noteService.update(_sampleNote.id, _sampleDto)).thenReturn(updated)
        val response: ResponseEntity<NoteDto> = _noteController.update(_sampleNote.id, _sampleDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated.toDto(), response.body)
        verify(_noteService).update(_sampleNote.id, _sampleDto)
    }

    @Test
    fun `should delete note with status code 204 No Content`() {
        doNothing().whenever(_noteService).delete(_sampleNote.id)
        val response: ResponseEntity<Void> = _noteController.delete(_sampleNote.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(_noteService).delete(_sampleNote.id)
    }

}
