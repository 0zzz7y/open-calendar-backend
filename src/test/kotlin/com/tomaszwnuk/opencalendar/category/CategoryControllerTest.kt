package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.category.*
import com.tomaszwnuk.opencalendar.domain.event.Event
import com.tomaszwnuk.opencalendar.domain.event.EventDto
import com.tomaszwnuk.opencalendar.domain.event.EventService
import com.tomaszwnuk.opencalendar.domain.note.Note
import com.tomaszwnuk.opencalendar.domain.note.NoteDto
import com.tomaszwnuk.opencalendar.domain.note.NoteService
import com.tomaszwnuk.opencalendar.domain.other.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.task.Task
import com.tomaszwnuk.opencalendar.domain.task.TaskDto
import com.tomaszwnuk.opencalendar.domain.task.TaskService
import com.tomaszwnuk.opencalendar.domain.task.TaskStatus
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.awt.Color
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryControllerTest {

    @Mock
    private lateinit var _categoryService: CategoryService

    @Mock
    private lateinit var _eventService: EventService

    @Mock
    private lateinit var _taskService: TaskService

    @Mock
    private lateinit var _noteService: NoteService

    @InjectMocks
    private lateinit var _categoryController: CategoryController

    private lateinit var _sampleCategory: Category

    private lateinit var _sampleCategoryDto: CategoryDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Personal",
            color = CategoryColorHelper.toHex(Color.GREEN)
        )
        _sampleCategoryDto = _sampleCategory.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created category with status code 201 Created`() {
        whenever(_categoryService.create(_sampleCategoryDto)).thenReturn(_sampleCategoryDto)

        val response: ResponseEntity<CategoryDto> = _categoryController.create(_sampleCategoryDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleCategoryDto, response.body)

        verify(_categoryService).create(_sampleCategoryDto)
    }

    @Test
    fun `should return paginated list of all categories with status code 200 OK`() {
        val categories: List<Category> = listOf(_sampleCategory, _sampleCategory.copy(), _sampleCategory.copy())
        whenever(_categoryService.getAll()).thenReturn(categories.map {it.toDto()})

        val response: ResponseEntity<Page<CategoryDto>> = _categoryController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size.toLong(), response.body?.totalElements)
        assertEquals(categories.map { it.id }, response.body?.content?.map { it.id })
        assertEquals(categories.map { it.title }, response.body?.content?.map { it.title })

        verify(_categoryService).getAll()
    }

    @Test
    fun `should return category by id with status code 200 OK`() {
        val id: UUID = _sampleCategory.id
        whenever(_categoryService.getById(id)).thenReturn(_sampleCategoryDto)

        val response: ResponseEntity<CategoryDto> = _categoryController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(_sampleCategoryDto, response.body)

        verify(_categoryService).getById(id)
    }

    @Test
    fun `should return paginated list of filtered categories with status code 200 OK`() {
        val filter = CategoryFilterDto(title = "Personal")
        val categories: List<Category> = listOf(_sampleCategory, _sampleCategory.copy(), _sampleCategory.copy())
        whenever(_categoryService.filter(filter)).thenReturn(categories.map{it.toDto()})

        val response: ResponseEntity<Page<CategoryDto>> = _categoryController.filter(filter.title, null, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size.toLong(), response.body?.totalElements)

        verify(_categoryService).filter(eq(filter))
    }

    @Test
    fun `should return paginated list of all category events with status code 200 OK`() {
        val id: UUID = _sampleCategory.id
        val calendar = Calendar(title = "Calendar", emoji = "📅")
        val event = Event(
            id = UUID.randomUUID(),
            title = "Event title",
            description = "Event description",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = calendar,
            category = _sampleCategory
        ).toDto()
        val events: List<EventDto> = listOf(event)

        whenever(_eventService.getAllByCategoryId(id)).thenReturn(events)

        val response: ResponseEntity<Page<EventDto>> = _categoryController.getEvents(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size.toLong(), response.body?.totalElements)
        assertEquals(events.map { it.title }, response.body?.content?.map { it.title })

        verify(_eventService).getAllByCategoryId(id)
    }

    @Test
    fun `should return paginated list of all category tasks with status code 200 OK`() {
        val id: UUID = _sampleCategory.id
        val calendar = Calendar(id = UUID.randomUUID(), title = "Calendar", emoji = "📅")
        val task = Task(
            id = UUID.randomUUID(),
            title = "Task title",
            description = "Task description",
            status = TaskStatus.TODO,
            calendar = calendar,
            category = _sampleCategory
        ).toDto()
        val tasks: List<TaskDto> = listOf(task)

        whenever(_taskService.getAllByCategoryId(id)).thenReturn(tasks)

        val response: ResponseEntity<Page<TaskDto>> = _categoryController.getTasks(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size.toLong(), response.body?.totalElements)
        assertEquals(tasks.map { it.title }, response.body?.content?.map { it.title })

        verify(_taskService).getAllByCategoryId(id)
    }

    @Test
    fun `should return paginated list of all category notes with status code 200 OK`() {
        val id: UUID = _sampleCategory.id
        val calendar = Calendar(id = UUID.randomUUID(), title = "Calendar", emoji = "📅")
        val note = Note(
            id = UUID.randomUUID(),
            title = "Note title",
            description = "Note description",
            calendar = calendar,
            category = _sampleCategory
        ).toDto()
        val notes: List<NoteDto> = listOf(note)

        whenever(_noteService.getAllByCategoryId(id)).thenReturn(notes)

        val response: ResponseEntity<Page<NoteDto>> = _categoryController.getNotes(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size.toLong(), response.body?.totalElements)
        assertEquals(notes.map { it.title }, response.body?.content?.map { it.title })

        verify(_noteService).getAllByCategoryId(id)
    }

    @Test
    fun `should return combined list of all category events, tasks and notes with status code 200 OK`() {
        val id: UUID = _sampleCategory.id
        val now: LocalDateTime = LocalDateTime.now()
        val calendar = Calendar(id = UUID.randomUUID(), title = "Calendar", emoji = "📅")

        val event = Event(
            id = UUID.randomUUID(),
            title = "Sample Event",
            description = "Event description",
            startDate = now,
            endDate = now.plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = calendar,
            category = _sampleCategory
        ).toDto()
        val task = Task(
            id = UUID.randomUUID(),
            title = "Sample Task",
            description = "Task description",
            status = TaskStatus.TODO,
            calendar = calendar,
            category = _sampleCategory
        ).toDto()
        val note = Note(
            id = UUID.randomUUID(),
            title = "Sample Note",
            description = "Note description",
            calendar = calendar,
            category = _sampleCategory
        ).toDto()

        whenever(_eventService.getAllByCategoryId(id)).thenReturn(listOf(event))
        whenever(_taskService.getAllByCategoryId(id)).thenReturn(listOf(task))
        whenever(_noteService.getAllByCategoryId(id)).thenReturn(listOf(note))

        val response: ResponseEntity<List<Map<String, Any>>> = _categoryController.getAllItems(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.size)

        val types: List<String> = response.body?.mapNotNull { it["type"] as? String } ?: emptyList()
        assertTrue(types.containsAll(listOf("event", "task", "note")))

        verify(_eventService).getAllByCategoryId(id)
        verify(_taskService).getAllByCategoryId(id)
        verify(_noteService).getAllByCategoryId(id)
    }

    @Test
    fun `should return updated category with status code 200 OK`() {
        val updatedCategory: Category = _sampleCategory.copy(title = "Work")
        whenever(_categoryService.update(_sampleCategory.id, _sampleCategoryDto)).thenReturn(updatedCategory.toDto())

        val response: ResponseEntity<CategoryDto> = _categoryController.update(_sampleCategory.id, _sampleCategoryDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedCategory.toDto(), response.body)

        verify(_categoryService).update(_sampleCategory.id, _sampleCategoryDto)
    }

    @Test
    fun `should delete category with status code 204 No Content`() {
        doNothing().whenever(_categoryService).delete(_sampleCategory.id)

        val response: ResponseEntity<Void> = _categoryController.delete(_sampleCategory.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(_categoryService).delete(_sampleCategory.id)
    }

}
