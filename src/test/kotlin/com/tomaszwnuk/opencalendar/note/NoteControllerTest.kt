package com.tomaszwnuk.opencalendar.note

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.note.NoteController
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteFilterDto
import com.tomaszwnuk.opencalendar.domain.note.NoteService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
internal class NoteControllerTest {

    @Mock
    private lateinit var _service: NoteService

    @InjectMocks
    private lateinit var _controller: NoteController

    private lateinit var _dto: NoteDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setUp() {
        _dto = NoteDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            calendarId = UUID.randomUUID(),
            categoryId = UUID.randomUUID()
        )
        _pageable = PageRequest.of(
            PAGEABLE_PAGE_NUMBER,
            PAGEABLE_PAGE_SIZE
        )
    }

    @Test
    fun `should return created note with status code 201 Created`() {
        whenever(_service.create(_dto)).thenReturn(_dto)

        val response: ResponseEntity<NoteDto> = _controller.create(dto = _dto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_dto, response.body)

        verify(_service).create(eq(_dto))
    }

    @Test
    fun `should return all notes with status code 200 OK`() {
        val dtos: List<NoteDto> = listOf(_dto, _dto.copy(), _dto.copy())
        whenever(_service.getAll()).thenReturn(dtos)

        val response: ResponseEntity<Page<NoteDto>> = _controller.getAll(pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(dtos.size.toLong(), response.body?.totalElements)
        assertEquals(dtos, response.body?.content)

        verify(_service).getAll()
    }

    @Test
    fun `should return note by id with status code 200 OK`() {
        val id: UUID = _dto.id!!
        whenever(_service.getById(id = id)).thenReturn(_dto)

        val response: ResponseEntity<NoteDto> = _controller.getById(id = id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(_dto, response.body)

        verify(_service).getById(id = id)
    }

    @Test
    fun `should return filtered notes with status code 200 OK`() {
        val name = "Test"
        val description = "Test"
        val filter = NoteFilterDto(
            name = name,
            description = description,
            calendarId = _dto.calendarId,
            categoryId = _dto.categoryId
        )
        val filteredDtos: List<NoteDto> = listOf(_dto)

        whenever(_service.filter(filter = filter)).thenReturn(filteredDtos)

        val response: ResponseEntity<Page<NoteDto>> = _controller.filter(
            name = name,
            description = description,
            calendarId = _dto.calendarId,
            categoryId = _dto.categoryId,
            pageable = _pageable
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(filteredDtos.size.toLong(), response.body?.totalElements)
        assertEquals(filteredDtos, response.body?.content)

        verify(_service).filter(filter = filter)
    }

    @Test
    fun `should update note with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val updated: NoteDto = _dto.copy(id = UUID.randomUUID())

        whenever(_service.update(id = id, dto = updated)).thenReturn(updated)

        val response: ResponseEntity<NoteDto> = _controller.update(id = id, dto = updated)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated, response.body)

        verify(_service).update(id = id, dto = updated)
    }

    @Test
    fun `should delete note with status code 204 No Content`() {
        val id: UUID = _dto.id!!
        doNothing().whenever(_service).delete(id = id)

        val response: ResponseEntity<Void> = _controller.delete(id = id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_service).delete(id = id)
    }

}
