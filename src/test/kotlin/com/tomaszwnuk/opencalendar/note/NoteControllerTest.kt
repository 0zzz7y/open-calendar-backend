package com.tomaszwnuk.opencalendar.note

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.category.CategoryColorHelper
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
import java.util.*

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NoteControllerTest {

    @Mock
    private lateinit var noteService: NoteService

    @InjectMocks
    private lateinit var noteController: NoteController

    private lateinit var sampleNote: Note

    private lateinit var sampleNoteDto: NoteDto

    private lateinit var pageable: Pageable

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
    fun `should return created note with status code 201 Created`() {
        whenever(noteService.create(sampleNoteDto)).thenReturn(sampleNote)

        val response: ResponseEntity<NoteDto> = noteController.create(sampleNoteDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertNotNull(response.body)
        assertEquals(sampleNote.id, response.body?.id)
        assertEquals(sampleNote.title, response.body?.title)
        assertEquals(sampleNote.description, response.body?.description)

        verify(noteService).create(sampleNoteDto)
    }

    @Test
    fun `should return paginated list of all notes with status code 200 OK`() {
        val notes: List<Note> = listOf(sampleNote, sampleNote.copy(), sampleNote.copy())
        whenever(noteService.getAll(pageable)).thenReturn(PageImpl(notes))

        val response: ResponseEntity<Page<NoteDto>> = noteController.getAll(pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size.toLong(), response.body?.totalElements)
        assertEquals(notes.map { it.id }, response.body?.content?.map { it.id })
        assertEquals(notes.map { it.title }, response.body?.content?.map { it.title })

        verify(noteService).getAll(pageable)
    }

    @Test
    fun `should return note by id with status code 200 OK`() {
        val id: UUID = sampleNote.id
        whenever(noteService.getById(id)).thenReturn(sampleNote)

        val response: ResponseEntity<NoteDto> = noteController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(sampleNote.id, response.body?.id)
        assertEquals(sampleNote.title, response.body?.title)
        assertEquals(sampleNote.description, response.body?.description)

        verify(noteService).getById(id)
    }

    @Test
    fun `should return paginated list of filtered notes with status code 200 OK`() {
        val filter = NoteFilterDto(title = "Groceries")
        val notes: List<Note> = listOf(sampleNote, sampleNote.copy(), sampleNote.copy())

        whenever(noteService.filter(eq(filter), eq(pageable))).thenReturn(PageImpl(notes))

        val response: ResponseEntity<Page<NoteDto>> = noteController.filter(
            filter.title,
            null,
            null,
            null,
            pageable
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size.toLong(), response.body?.totalElements)
        assertEquals(notes.map { it.title }, response.body?.content?.map { it.title })

        verify(noteService).filter(eq(filter), eq(pageable))
    }

    @Test
    fun `should return updated note with status code 200 OK`() {
        val updatedNote: Note = sampleNote.copy(title = "Updated Note")
        whenever(noteService.update(sampleNote.id, sampleNoteDto)).thenReturn(updatedNote)

        val response: ResponseEntity<NoteDto> = noteController.update(sampleNote.id, sampleNoteDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertNotNull(response.body)
        assertEquals(updatedNote.id, response.body?.id)
        assertEquals("Updated Note", response.body?.title)
        assertEquals(updatedNote.description, response.body?.description)

        verify(noteService).update(sampleNote.id, sampleNoteDto)
    }

    @Test
    fun `should delete note with status code 204 No Content`() {
        doNothing().whenever(noteService).delete(sampleNote.id)

        val response: ResponseEntity<Void> = noteController.delete(sampleNote.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(noteService).delete(sampleNote.id)
    }

}
