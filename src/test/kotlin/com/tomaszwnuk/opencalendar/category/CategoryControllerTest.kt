package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarDto
import com.tomaszwnuk.opencalendar.domain.category.*
import com.tomaszwnuk.opencalendar.domain.event.EventDto
import com.tomaszwnuk.opencalendar.domain.event.EventService
import com.tomaszwnuk.opencalendar.domain.event.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteService
import com.tomaszwnuk.opencalendar.domain.task.TaskDto
import com.tomaszwnuk.opencalendar.domain.task.TaskService
import com.tomaszwnuk.opencalendar.domain.task.TaskStatus
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
import java.awt.Color
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
internal class CategoryControllerTest {

    @Mock
    private lateinit var _service: CategoryService

    @Mock
    private lateinit var _eventService: EventService

    @Mock
    private lateinit var _taskService: TaskService

    @Mock
    private lateinit var _noteService: NoteService

    @InjectMocks
    private lateinit var _controller: CategoryController

    private lateinit var _dto: CategoryDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setUp() {
        _dto = CategoryDto(
            id = UUID.randomUUID(),
            name = "Test",
            color = CategoryColorHelper.toHex(Color.BLUE),
        )
        _pageable = PageRequest.of(
            PAGEABLE_PAGE_NUMBER,
            PAGEABLE_PAGE_SIZE
        )
    }

    @Test
    fun `should return created category with status code 201 Created`() {
        whenever(_service.create(_dto)).thenReturn(_dto)

        val response: ResponseEntity<CategoryDto> = _controller.create(dto = _dto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_dto, response.body)

        verify(_service).create(eq(_dto))
    }

    @Test
    fun `should return paginated list of all categories with status code 200 OK`() {
        val dtos: List<CategoryDto> = listOf(_dto, _dto.copy(), _dto.copy())
        whenever(_service.getAll()).thenReturn(dtos)

        val response: ResponseEntity<Page<CategoryDto>> = _controller.getAll(pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(dtos.size.toLong(), response.body?.totalElements)
        assertEquals(dtos, response.body?.content)

        verify(_service).getAll()
    }

    @Test
    fun `should return category by id with status code 200 OK`() {
        val id: UUID = _dto.id!!
        whenever(_service.getById(id = id)).thenReturn(_dto)

        val response: ResponseEntity<CategoryDto> = _controller.getById(id = id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(_dto, response.body)

        verify(_service).getById(id = id)
    }

    @Test
    fun `should return paginated list of category events with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val now: LocalDateTime = LocalDateTime.now()
        val calendar = CalendarDto(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢"
        )
        val event = EventDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendarId = calendar.id!!,
            categoryId = id
        )
        val eventsList: List<EventDto> = listOf(event, event.copy(), event.copy())

        whenever(_eventService.getAllByCategoryId(categoryId = id)).thenReturn(eventsList)

        val response: ResponseEntity<Page<EventDto>> = _controller.getEvents(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(eventsList.size.toLong(), response.body?.totalElements)
        assertEquals(eventsList, response.body?.content)

        verify(_eventService).getAllByCategoryId(categoryId = id)
    }

    @Test
    fun `should return paginated list of category tasks with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val calendar = CalendarDto(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢"
        )
        val task = TaskDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            status = TaskStatus.TODO,
            calendarId = calendar.id!!,
            categoryId = id
        )
        val tasksList: List<TaskDto> = listOf(task, task.copy(), task.copy())

        whenever(_taskService.getAllByCategoryId(categoryId = id)).thenReturn(tasksList)

        val response: ResponseEntity<Page<TaskDto>> = _controller.getTasks(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasksList.size.toLong(), response.body?.totalElements)
        assertEquals(tasksList, response.body?.content)

        verify(_taskService).getAllByCategoryId(categoryId = id)
    }

    @Test
    fun `should return paginated list of category notes with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val calendar = CalendarDto(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢"
        )
        val note = NoteDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            calendarId = calendar.id!!,
            categoryId = id
        )
        val notesList: List<NoteDto> = listOf(note, note.copy(), note.copy())

        whenever(_noteService.getAllByCategoryId(categoryId = id)).thenReturn(notesList)

        val response: ResponseEntity<Page<NoteDto>> = _controller.getNotes(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notesList.size.toLong(), response.body?.totalElements)
        assertEquals(notesList, response.body?.content)

        verify(_noteService).getAllByCategoryId(categoryId = id)
    }

    @Test
    fun `should return combined list of items with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val now: LocalDateTime = LocalDateTime.now()
        val calendar = CalendarDto(
            id = UUID.randomUUID(),
            name = "Test",
            emoji = "🟢"
        )
        val calendarId: UUID = calendar.id!!
        val event = EventDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendarId = calendarId,
            categoryId = id
        )
        val task = TaskDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            status = TaskStatus.TODO,
            calendarId = calendarId,
            categoryId = id
        )
        val note = NoteDto(
            id = UUID.randomUUID(),
            name = "Test",
            description = "Test",
            calendarId = calendarId,
            categoryId = id
        )

        whenever(_eventService.getAllByCategoryId(categoryId = id)).thenReturn(listOf(event))
        whenever(_taskService.getAllByCategoryId(categoryId = id)).thenReturn(listOf(task))
        whenever(_noteService.getAllByCategoryId(categoryId = id)).thenReturn(listOf(note))

        val response: ResponseEntity<List<Map<String, Any>>> = _controller.getAllItems(id = id, pageable = _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.size)

        val types = response.body?.mapNotNull { it["type"] as? String } ?: emptyList()
        assertTrue(types.containsAll(listOf("event", "task", "note")))

        verify(_eventService).getAllByCategoryId(categoryId = id)
        verify(_taskService).getAllByCategoryId(categoryId = id)
        verify(_noteService).getAllByCategoryId(categoryId = id)
    }

    @Test
    fun `should return paginated list of filtered categories with status code 200 OK`() {
        val name = "Test"
        val color = CategoryColorHelper.toHex(Color.BLUE)
        val filter = CategoryFilterDto(
            name = name,
            color = color
        )
        val filteredDtos: List<CategoryDto> = listOf(_dto)

        whenever(_service.filter(filter = filter)).thenReturn(filteredDtos)

        val response: ResponseEntity<Page<CategoryDto>> = _controller.filter(
            name = filter.name,
            color = filter.color,
            pageable = _pageable
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(filteredDtos.size.toLong(), response.body?.totalElements)
        assertEquals(filteredDtos, response.body?.content)

        verify(_service).filter(filter = filter)
    }

    @Test
    fun `should update category with status code 200 OK`() {
        val id: UUID = _dto.id!!
        val updated: CategoryDto = _dto.copy(id = UUID.randomUUID())

        whenever(_service.update(id = id, dto = updated)).thenReturn(updated)

        val response: ResponseEntity<CategoryDto> = _controller.update(id = id, dto = updated)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated, response.body)

        verify(_service).update(id = id, dto = updated)
    }

    @Test
    fun `should delete category with status code 204 No Content`() {
        val id: UUID = _dto.id!!
        doNothing().whenever(_service).delete(id = id)

        val response: ResponseEntity<Void> = _controller.delete(id = id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_service).delete(id = id)
    }

}
