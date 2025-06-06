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
import org.mockito.kotlin.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class NoteControllerTest {

    @Mock
    private lateinit var _noteService: NoteService

    @InjectMocks
    private lateinit var _controller: NoteController

    private lateinit var _pageable: Pageable

    private lateinit var _sampleId: UUID

    private lateinit var _sampleDto: NoteDto

    @BeforeEach
    fun setUp() {
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
        _sampleId = UUID.randomUUID()
        _sampleDto = NoteDto(
            id = _sampleId,
            name = "Weekly Summary",
            description = "Summary of weekly progress",
            calendarId = UUID.randomUUID(),
            categoryId = UUID.randomUUID()
        )
    }

    @Test
    fun `should create note with status code 201 Created`() {
        whenever(_noteService.create(dto = eq(_sampleDto))).thenReturn(_sampleDto)

        val response: ResponseEntity<NoteDto> = _controller.create(dto = _sampleDto)

        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body == _sampleDto)
        verify(_noteService).create(dto = eq(_sampleDto))
    }

    @Test
    fun `should return all notes with status code 200 OK`() {
        val note1 = _sampleDto.copy(id = UUID.randomUUID(), name = "Standup Notes")
        val note2 = _sampleDto.copy(id = UUID.randomUUID(), name = "Project Kickoff")
        whenever(_noteService.getAll()).thenReturn(listOf(note1, note2))

        val response: ResponseEntity<Page<NoteDto>> =
            _controller.getAll(pageable = _pageable)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.totalElements == 2L)
        val titles = response.body?.content?.map { it.name } ?: emptyList()
        assert(titles.containsAll(listOf("Standup Notes", "Project Kickoff")))
        verify(_noteService).getAll()
    }

    @Test
    fun `should return note by id with status code 200 OK`() {
        whenever(_noteService.getById(_sampleId)).thenReturn(_sampleDto)

        val response: ResponseEntity<NoteDto> = _controller.getById(_sampleId)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == _sampleDto)
        verify(_noteService).getById(_sampleId)
    }

    @Test
    fun `should return filtered notes with status code 200 OK`() {
        val filtered = _sampleDto.copy(id = UUID.randomUUID(), name = "Release Notes")
        whenever(_noteService.filter(any<NoteFilterDto>())).thenReturn(listOf(filtered))

        val response: ResponseEntity<Page<NoteDto>> = _controller.filter(
            name = "Release",
            description = "notes",
            calendarId = _sampleDto.calendarId,
            categoryId = _sampleDto.categoryId,
            pageable = _pageable
        )

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.totalElements == 1L)
        assert(response.body?.content?.first()?.name == "Release Notes")
        verify(_noteService).filter(any<NoteFilterDto>())
    }

    @Test
    fun `should update note with status code 200 OK`() {
        val updated = _sampleDto.copy(name = "Sprint Retrospective")
        whenever(_noteService.update(id = _sampleId, dto = _sampleDto)).thenReturn(updated)

        val response: ResponseEntity<NoteDto> = _controller.update(id = _sampleId, dto = _sampleDto)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == updated)
        verify(_noteService).update(id = _sampleId, dto = _sampleDto)
    }

    @Test
    fun `should delete note with status code 204 No Content`() {
        doNothing().whenever(_noteService).delete(id = _sampleId)

        val response: ResponseEntity<Void> = _controller.delete(id = _sampleId)

        assert(response.statusCode == HttpStatus.NO_CONTENT)
        verify(_noteService).delete(id = _sampleId)
    }

}
