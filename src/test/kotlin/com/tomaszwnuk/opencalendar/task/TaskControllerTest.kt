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
import org.mockito.kotlin.*
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class TaskControllerTest {

    @Mock
    private lateinit var _taskService: TaskService

    @InjectMocks
    private lateinit var _controller: TaskController

    private lateinit var _pageable: Pageable

    private lateinit var _sampleId: UUID

    private lateinit var _sampleDto: TaskDto

    @BeforeEach
    fun setUp() {
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
        _sampleId = UUID.randomUUID()
        _sampleDto = TaskDto(
            id = _sampleId,
            name = "Code Review",
            description = "Review PRs and provide feedback",
            status = TaskStatus.TODO,
            calendarId = UUID.randomUUID(),
            categoryId = UUID.randomUUID()
        )
    }

    @Test
    fun `should create task with status code 201 Created`() {
        whenever(_taskService.create(dto = eq(_sampleDto))).thenReturn(_sampleDto)

        val response: ResponseEntity<TaskDto> = _controller.create(dto = _sampleDto)

        assert(response.statusCode == HttpStatus.CREATED)
        assert(response.body == _sampleDto)
        verify(_taskService).create(eq(_sampleDto))
    }

    @Test
    fun `should return all tasks with status code 200 OK`() {
        val taskOne =
            _sampleDto.copy(id = UUID.randomUUID(), name = "Release Planning", status = TaskStatus.IN_PROGRESS)
        val taskTwo = _sampleDto.copy(id = UUID.randomUUID(), name = "Sprint Retrospective", status = TaskStatus.DONE)
        whenever(_taskService.getAll()).thenReturn(listOf(taskOne, taskTwo))

        val response: ResponseEntity<Page<TaskDto>> =
            _controller.getAll(pageable = _pageable)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.totalElements == 2L)
        val titles = response.body?.content?.map { it.name } ?: emptyList()
        assert(titles.containsAll(listOf("Release Planning", "Sprint Retrospective")))
        verify(_taskService).getAll()
    }

    @Test
    fun `should return task by id with status code 200 OK`() {
        whenever(_taskService.getById(id = _sampleId)).thenReturn(_sampleDto)

        val response: ResponseEntity<TaskDto> = _controller.getById(id = _sampleId)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == _sampleDto)
        verify(_taskService).getById(id = _sampleId)
    }

    @Test
    fun `should return filtered tasks with status code 200 OK`() {
        val filteredDto = _sampleDto.copy(id = UUID.randomUUID(), name = "Deployment")
        whenever(_taskService.filter(any<TaskFilterDto>())).thenReturn(listOf(filteredDto))

        val response: ResponseEntity<Page<TaskDto>> = _controller.filter(
            name = "Deploy",
            description = "release",
            status = TaskStatus.TODO.name,
            calendarId = _sampleDto.calendarId,
            categoryId = _sampleDto.categoryId,
            pageable = _pageable
        )

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.totalElements == 1L)
        assert(response.body?.content?.first()?.name == "Deployment")
        verify(_taskService).filter(any<TaskFilterDto>())
    }

    @Test
    fun `should update task with status code 200 OK`() {
        val updatedDto = _sampleDto.copy(status = TaskStatus.IN_PROGRESS)
        whenever(_taskService.update(id = _sampleId, dto = _sampleDto)).thenReturn(updatedDto)

        val response: ResponseEntity<TaskDto> = _controller.update(id = _sampleId, dto = _sampleDto)

        assert(response.statusCode == HttpStatus.OK)
        assert(response.body == updatedDto)
        verify(_taskService).update(_sampleId, _sampleDto)
    }

    @Test
    fun `should delete task with status code 204 No Content`() {
        doNothing().whenever(_taskService).delete(id = _sampleId)

        val response: ResponseEntity<Void> = _controller.delete(id = _sampleId)

        assert(response.statusCode == HttpStatus.NO_CONTENT)
        verify(_taskService).delete(id = _sampleId)
    }

}
