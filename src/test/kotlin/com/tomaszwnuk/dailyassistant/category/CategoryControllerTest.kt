package com.tomaszwnuk.dailyassistant.category

import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.event.Event
import com.tomaszwnuk.dailyassistant.event.EventDto
import com.tomaszwnuk.dailyassistant.event.EventRepository
import com.tomaszwnuk.dailyassistant.note.Note
import com.tomaszwnuk.dailyassistant.note.NoteDto
import com.tomaszwnuk.dailyassistant.note.NoteRepository
import com.tomaszwnuk.dailyassistant.task.Task
import com.tomaszwnuk.dailyassistant.task.TaskDto
import com.tomaszwnuk.dailyassistant.task.TaskRepository
import com.tomaszwnuk.dailyassistant.task.TaskStatus
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
import java.awt.Color
import java.time.LocalDateTime
import java.util.*
import kotlin.test.assertTrue

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CategoryControllerTest {

    @Mock
    private lateinit var _categoryService: CategoryService

    @Mock
   private lateinit var _eventRepository: EventRepository

    @Mock
    private lateinit var _taskRepository: TaskRepository

    @Mock
    private lateinit var _noteRepository: NoteRepository

    @InjectMocks
    private lateinit var _categoryController: CategoryController

    private lateinit var _sampleCategory: Category

    private lateinit var _sampleDto: CategoryDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            name = "Personal",
            color = CategoryColors.toHex(Color.GREEN)
        )
        _sampleDto = _sampleCategory.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created category with status code 201 Created`() {
        whenever(_categoryService.create(any())).thenReturn(_sampleCategory)
        val response: ResponseEntity<CategoryDto> = _categoryController.create(_sampleDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(_sampleDto, response.body)
        verify(_categoryService).create(_sampleDto)
    }

    @Test
    fun `should return paginated list of events with status code 200 OK`() {
        val categories: List<Category> = listOf(_sampleCategory, _sampleCategory, _sampleCategory)

        whenever(_categoryService.getAll(_pageable)).thenReturn(PageImpl(categories))
        val response: ResponseEntity<Page<CategoryDto>> = _categoryController.getAll(_pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size, response.body?.totalElements?.toInt())
        verify(_categoryService).getAll(_pageable)
    }

    @Test
    fun `should return category by id with status code 200 OK`() {
        val id: UUID = _sampleCategory.id

        whenever(_categoryService.getById(id)).thenReturn(_sampleCategory)
        val response: ResponseEntity<CategoryDto> = _categoryController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(_sampleDto, response.body)
        verify(_categoryService).getById(id)
    }

    @Test
    fun `should return filtered categories with status code 200 OK`() {
        val filter = CategoryFilterDto(name = "Personal")
        val categories: List<Category> = listOf(_sampleCategory, _sampleCategory, _sampleCategory)

        whenever(_categoryService.filter(filter, _pageable)).thenReturn(PageImpl(categories))
        val response: ResponseEntity<Page<CategoryDto>> = _categoryController.filter(
            eq(filter.name),
            null,
            eq(_pageable)
        )

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size, response.body?.totalElements?.toInt())
        verify(_categoryService).filter(eq(filter), eq(_pageable))
    }

    @Test
    fun `should return events by category id with status code 200 OK`() {
        val id: UUID = _sampleCategory.id
        val sampleCalendar = Calendar(name = "Calendar")
        val event = Event(
            id = UUID.randomUUID(),
            name = "Event",
            description = "Description",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = sampleCalendar,
            category = _sampleCategory
        )
        val events: List<Event> = listOf(event)

        whenever(_eventRepository.findAllByCategoryId(id, _pageable)).thenReturn(PageImpl(events))
        val response: ResponseEntity<Page<EventDto>> = _categoryController.getEvents(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size, response.body?.totalElements?.toInt())
        assertEquals(event.name, response.body?.content?.first()?.name)
    }

    @Test
    fun `should return tasks by category id with status code 200 OK`() {
        val id: UUID = _sampleCategory.id
        val sampleCalendar = Calendar(name = "Calendar")
        val task = Task(
            id = UUID.randomUUID(),
            name = "Task",
            description = "Description",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = _sampleCategory
        )
        val tasks: List<Task> = listOf(task)

        whenever(_taskRepository.findAllByCategoryId(id, _pageable)).thenReturn(PageImpl(tasks))
        val response: ResponseEntity<Page<TaskDto>> = _categoryController.getTasks(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size, response.body?.totalElements?.toInt())
        assertEquals(task.name, response.body?.content?.first()?.name)
    }

    @Test
    fun `should return notes by category id with status code 200 OK`() {
        val id: UUID = _sampleCategory.id
        val note = Note(
            id = UUID.randomUUID(),
            name = "Note",
            description = "Description",
            category = _sampleCategory
        )
        val notes: List<Note> = listOf(note)

        whenever(_noteRepository.findAllByCategoryId(id, _pageable)).thenReturn(PageImpl(notes))
        val response: ResponseEntity<Page<NoteDto>> = _categoryController.getNotes(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size, response.body?.size)
        assertEquals(note.name, response.body?.content?.first()?.name)
    }

    @Test
    fun `should return category items by category id with status code 200 OK`() {
        val id: UUID = _sampleCategory.id
        val sampleCalendar = Calendar(name = "Calendar")
        val event = Event(
            id = UUID.randomUUID(),
            name = "Event",
            description = "Description",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = sampleCalendar,
            category = _sampleCategory
        )
        val task = Task(
            id = UUID.randomUUID(),
            name = "Task",
            description = "Description",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = _sampleCategory
        )
        val note = Note(
            id = UUID.randomUUID(),
            name = "Note",
            description = "Description",
            category = _sampleCategory
        )

        whenever(_eventRepository.findAllByCategoryId(id, _pageable)).thenReturn(PageImpl(listOf(event)))
        whenever(_taskRepository.findAllByCategoryId(id, _pageable)).thenReturn(PageImpl(listOf(task)))
        whenever(_noteRepository.findAllByCategoryId(id, _pageable)).thenReturn(PageImpl(listOf(note)))
        val response: ResponseEntity<List<Map<String, Any>>> = _categoryController.getAllItems(id, _pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.size)

        val types = response.body?.map { it["type"] }
        assertTrue(types?.containsAll(listOf("event", "task", "note")) ?: false)
    }

    @Test
    fun `should return updated category by id with status code 200 OK`() {
        val updated: Category = _sampleCategory.copy(name = "Work")

        whenever(_categoryService.update(_sampleCategory.id, _sampleDto)).thenReturn(updated)
        val response: ResponseEntity<CategoryDto> = _categoryController.update(_sampleCategory.id, _sampleDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updated.toDto(), response.body)
        verify(_categoryService).update(_sampleCategory.id, _sampleDto)
    }

    @Test
    fun `should delete category by id with status code 204 No Content`() {
        doNothing().whenever(_categoryService).delete(_sampleCategory.id)
        val response: ResponseEntity<Void> = _categoryController.delete(_sampleCategory.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)
        verify(_categoryService).delete(_sampleCategory.id)
    }

}
