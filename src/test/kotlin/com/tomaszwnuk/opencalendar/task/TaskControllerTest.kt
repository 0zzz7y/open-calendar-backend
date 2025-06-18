package com.tomaszwnuk.opencalendar.task

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.task.*
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
internal class TaskControllerTest {

    @Mock
    private lateinit var _service: TaskService

    @InjectMocks
    private lateinit var _controller: TaskController

    private lateinit var _sampleDto: TaskDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setUp() {
        _sampleDto = TaskDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            status = TaskStatus.TODO,
            calendarId = UUID.randomUUID(),
            categoryId = UUID.randomUUID()
        )
        _pageable = PageRequest.of(
            PAGEABLE_PAGE_NUMBER,
            PAGEABLE_PAGE_SIZE
        )
    }

    @Test
    fun `should return created task with status code 201 Created`() {
        whenever(_service.create(_sampleDto)).thenReturn(_sampleDto)

        val response: ResponseEntity<TaskDto> = _controller.create(dto = _sampleDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleDto, response.body)

        verify(_service).create(eq(_sampleDto))
    }

    @Test
    fun `should return all tasks with status code 200 OK`() {
        val dtos: List<TaskDto> = listOf(_sampleDto, _sampleDto.copy(), _sampleDto.copy())
        whenever(_service.getAll()).thenReturn(dtos)

        val response: ResponseEntity<Page<TaskDto>> = _controller.getAll(pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(dtos.size.toLong(), response.body?.totalElements)
        assertEquals(dtos, response.body?.content)

        verify(_service).getAll()
    }

    @Test
    fun `should return task by id with status code 200 OK`() {
        val id: UUID = _sampleDto.id!!
        whenever(_service.getById(id = id)).thenReturn(_sampleDto)

        val response: ResponseEntity<TaskDto> = _controller.getById(id = id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(_sampleDto, response.body)

        verify(_service).getById(id = id)
    }

    @Test
    fun `should return filtered tasks with status code 200 OK`() {
        val name = "Test"
        val description = "Test"
        val status: TaskStatus = TaskStatus.TODO
        val filter = TaskFilterDto(
            name = name,
            description = description,
            status = status,
            calendarId = _sampleDto.calendarId,
            categoryId = _sampleDto.categoryId
        )
        val filteredDtos: List<TaskDto> = listOf(_sampleDto)

        whenever(_service.filter(filter = filter)).thenReturn(filteredDtos)

        val response: ResponseEntity<Page<TaskDto>> = _controller.filter(
            name = name,
            description = description,
            status = status.toString(),
            calendarId = _sampleDto.calendarId,
            categoryId = _sampleDto.categoryId,
            pageable = _pageable
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(filteredDtos.size.toLong(), response.body?.totalElements)
        assertEquals(filteredDtos, response.body?.content)

        verify(_service).filter(filter = filter)
    }

    @Test
    fun `should update task with status code 200 OK`() {
        val id: UUID = _sampleDto.id!!
        val updated: TaskDto = _sampleDto.copy(id = UUID.randomUUID())

        whenever(_service.update(id = id, dto = updated)).thenReturn(updated)

        val response: ResponseEntity<TaskDto> = _controller.update(id = id, dto = updated)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated, response.body)

        verify(_service).update(id = id, dto = updated)
    }

    @Test
    fun `should delete task with status code 204 No Content`() {
        val id: UUID = _sampleDto.id!!
        doNothing().whenever(_service).delete(id = id)

        val response: ResponseEntity<Void> = _controller.delete(id = id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_service).delete(id = id)
    }

}
