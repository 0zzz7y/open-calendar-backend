/*
 * Copyright (c) Tomasz Wnuk
 */

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
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.UUID

/**
 * Unit tests for the `NoteController` class.
 * Verifies the behavior of the controller's endpoints using mocked dependencies.
 */
@ExtendWith(MockitoExtension::class)
internal class NoteControllerTest {

    /**
     * Mocked instance of `NoteService` for simulating note-related operations.
     */
    @Mock
    private lateinit var _noteService: NoteService

    /**
     * Injected instance of `NoteController` with mocked dependencies.
     */
    @InjectMocks
    private lateinit var _controller: NoteController

    /**
     * Pageable instance for simulating pagination in tests.
     */
    private lateinit var _pageable: Pageable

    /**
     * Sample UUID used for testing.
     */
    private lateinit var _sampleId: UUID

    /**
     * Sample `NoteDto` instance used in tests.
     */
    private lateinit var _sampleDto: NoteDto

    /**
     * Sets up the test environment before each test.
     * Initializes `Pageable`, sample UUID, and sample `NoteDto`.
     */
    @BeforeEach
    fun setUp() {
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
        _sampleId = UUID.randomUUID()
        _sampleDto = NoteDto(
            id = _sampleId,
            title = "Weekly Summary",
            description = "Summary of weekly progress",
            calendarId = UUID.randomUUID(),
            categoryId = UUID.randomUUID()
        )
    }

    /**
     * Tests the creation of a note.
     * Verifies that the endpoint returns a 201 Created status and the created note.
     */
    @Test
    fun `should create note with status code 201 Created`() {
        whenever(_noteService.create(eq(_sampleDto))).thenReturn(_sampleDto)

        val response: ResponseEntity<NoteDto> = _controller.create(_sampleDto)

        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body == _sampleDto)
        verify(_noteService).create(eq(_sampleDto))
    }

    /**
     * Tests retrieving all notes.
     * Verifies that the endpoint returns a 200 OK status and a list of notes.
     */
    @Test
    fun `should return all notes with status code 200 OK`() {
        val note1 = _sampleDto.copy(id = UUID.randomUUID(), title = "Standup Notes")
        val note2 = _sampleDto.copy(id = UUID.randomUUID(), title = "Project Kickoff")
        whenever(_noteService.getAll()).thenReturn(listOf(note1, note2))

        val response: ResponseEntity<Page<NoteDto>> =
            _controller.getAll(_pageable)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.totalElements == 2L)
        val titles = response.body?.content?.map { it.title } ?: emptyList()
        assert(titles.containsAll(listOf("Standup Notes", "Project Kickoff")))
        verify(_noteService).getAll()
    }

    /**
     * Tests retrieving a note by its ID.
     * Verifies that the endpoint returns a 200 OK status and the requested note.
     */
    @Test
    fun `should return note by id with status code 200 OK`() {
        whenever(_noteService.getById(_sampleId)).thenReturn(_sampleDto)

        val response: ResponseEntity<NoteDto> = _controller.getById(_sampleId)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == _sampleDto)
        verify(_noteService).getById(_sampleId)
    }

    /**
     * Tests filtering notes based on criteria.
     * Verifies that the endpoint returns a 200 OK status and a list of filtered notes.
     */
    @Test
    fun `should return filtered notes with status code 200 OK`() {
        val filtered = _sampleDto.copy(id = UUID.randomUUID(), title = "Release Notes")
        whenever(_noteService.filter(any<NoteFilterDto>())).thenReturn(listOf(filtered))

        val response: ResponseEntity<Page<NoteDto>> = _controller.filter(
            title = "Release",
            description = "notes",
            calendarId = _sampleDto.calendarId,
            categoryId = _sampleDto.categoryId,
            pageable = _pageable
        )

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.totalElements == 1L)
        assert(response.body?.content?.first()?.title == "Release Notes")
        verify(_noteService).filter(any<NoteFilterDto>())
    }

    /**
     * Tests updating a note.
     * Verifies that the endpoint returns a 200 OK status and the updated note.
     */
    @Test
    fun `should update note with status code 200 OK`() {
        val updated = _sampleDto.copy(title = "Sprint Retrospective")
        whenever(_noteService.update(_sampleId, _sampleDto)).thenReturn(updated)

        val response: ResponseEntity<NoteDto> = _controller.update(_sampleId, _sampleDto)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == updated)
        verify(_noteService).update(_sampleId, _sampleDto)
    }

    /**
     * Tests deleting a note.
     * Verifies that the endpoint returns a 204 No Content status.
     */
    @Test
    fun `should delete note with status code 204 No Content`() {
        doNothing().whenever(_noteService).delete(_sampleId)

        val response: ResponseEntity<Void> = _controller.delete(_sampleId)

        assert(response.statusCode == HttpStatus.NO_CONTENT)
        verify(_noteService).delete(_sampleId)
    }

}
