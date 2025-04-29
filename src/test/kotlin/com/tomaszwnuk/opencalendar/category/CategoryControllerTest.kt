package com.tomaszwnuk.opencalendar.category

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.RecurringPattern
import com.tomaszwnuk.opencalendar.event.Event
import com.tomaszwnuk.opencalendar.event.EventDto
import com.tomaszwnuk.opencalendar.event.EventService
import com.tomaszwnuk.opencalendar.note.Note
import com.tomaszwnuk.opencalendar.note.NoteDto
import com.tomaszwnuk.opencalendar.note.NoteService
import com.tomaszwnuk.opencalendar.task.Task
import com.tomaszwnuk.opencalendar.task.TaskDto
import com.tomaszwnuk.opencalendar.task.TaskService
import com.tomaszwnuk.opencalendar.task.TaskStatus
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
import org.springframework.data.domain.PageImpl
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
    private lateinit var categoryService: CategoryService

    @Mock
    private lateinit var eventService: EventService

    @Mock
    private lateinit var taskService: TaskService

    @Mock
    private lateinit var noteService: NoteService

    @InjectMocks
    private lateinit var categoryController: CategoryController

    private lateinit var sampleCategory: Category

    private lateinit var sampleCategoryDto: CategoryDto

    private lateinit var pageable: Pageable

    @BeforeEach
    fun setup() {
        sampleCategory = Category(
            id = UUID.randomUUID(),
            title = "Personal",
            color = CategoryColorHelper.toHex(Color.GREEN)
        )
        sampleCategoryDto = sampleCategory.toDto()
        pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created category with status code 201 Created`() {
        whenever(categoryService.create(sampleCategoryDto)).thenReturn(sampleCategory)

        val response: ResponseEntity<CategoryDto> = categoryController.create(sampleCategoryDto)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(sampleCategoryDto, response.body)

        verify(categoryService).create(sampleCategoryDto)
    }

    @Test
    fun `should return paginated list of all categories with status code 200 OK`() {
        val categories: List<Category> = listOf(sampleCategory, sampleCategory.copy(), sampleCategory.copy())
        whenever(categoryService.getAll(pageable)).thenReturn(PageImpl(categories))

        val response: ResponseEntity<Page<CategoryDto>> = categoryController.getAll(pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size.toLong(), response.body?.totalElements)
        assertEquals(categories.map { it.id }, response.body?.content?.map { it.id })
        assertEquals(categories.map { it.title }, response.body?.content?.map { it.title })

        verify(categoryService).getAll(pageable)
    }

    @Test
    fun `should return category by id with status code 200 OK`() {
        val id: UUID = sampleCategory.id
        whenever(categoryService.getById(id)).thenReturn(sampleCategory)

        val response: ResponseEntity<CategoryDto> = categoryController.getById(id)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(sampleCategoryDto, response.body)

        verify(categoryService).getById(id)
    }

    @Test
    fun `should return paginated list of filtered categories with status code 200 OK`() {
        val filter = CategoryFilterDto(title = "Personal")
        val categories: List<Category> = listOf(sampleCategory, sampleCategory.copy(), sampleCategory.copy())
        whenever(categoryService.filter(filter, pageable)).thenReturn(PageImpl(categories))

        val response: ResponseEntity<Page<CategoryDto>> = categoryController.filter(filter.title, null, pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(categories.size.toLong(), response.body?.totalElements)

        verify(categoryService).filter(eq(filter), eq(pageable))
    }

    @Test
    fun `should return paginated list of all category events with status code 200 OK`() {
        val id: UUID = sampleCategory.id
        val calendar = Calendar(title = "Calendar", emoji = "📅")
        val event = Event(
            id = UUID.randomUUID(),
            title = "Event title",
            description = "Event description",
            startDate = LocalDateTime.now(),
            endDate = LocalDateTime.now().plusHours(1),
            recurringPattern = RecurringPattern.NONE,
            calendar = calendar,
            category = sampleCategory
        )
        val events: List<Event> = listOf(event)

        whenever(eventService.getAllByCategoryId(id, pageable)).thenReturn(PageImpl(events))

        val response: ResponseEntity<Page<EventDto>> = categoryController.getEvents(id, pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(events.size.toLong(), response.body?.totalElements)
        assertEquals(events.map { it.title }, response.body?.content?.map { it.title })

        verify(eventService).getAllByCategoryId(id, pageable)
    }

    @Test
    fun `should return paginated list of all category tasks with status code 200 OK`() {
        val id: UUID = sampleCategory.id
        val calendar = Calendar(id = UUID.randomUUID(), title = "Calendar", emoji = "📅")
        val task = Task(
            id = UUID.randomUUID(),
            title = "Task title",
            description = "Task description",
            status = TaskStatus.TODO,
            calendar = calendar,
            category = sampleCategory
        )
        val tasks: List<Task> = listOf(task)

        whenever(taskService.getAllByCategoryId(id, pageable)).thenReturn(PageImpl(tasks))

        val response: ResponseEntity<Page<TaskDto>> = categoryController.getTasks(id, pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(tasks.size.toLong(), response.body?.totalElements)
        assertEquals(tasks.map { it.title }, response.body?.content?.map { it.title })

        verify(taskService).getAllByCategoryId(id, pageable)
    }

    @Test
    fun `should return paginated list of all category notes with status code 200 OK`() {
        val id: UUID = sampleCategory.id
        val calendar = Calendar(id = UUID.randomUUID(), title = "Calendar", emoji = "📅")
        val note = Note(
            id = UUID.randomUUID(),
            title = "Note title",
            description = "Note description",
            calendar = calendar,
            category = sampleCategory
        )
        val notes: List<Note> = listOf(note)

        whenever(noteService.getAllByCategoryId(id, pageable)).thenReturn(PageImpl(notes))

        val response: ResponseEntity<Page<NoteDto>> = categoryController.getNotes(id, pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(notes.size.toLong(), response.body?.totalElements)
        assertEquals(notes.map { it.title }, response.body?.content?.map { it.title })

        verify(noteService).getAllByCategoryId(id, pageable)
    }

    @Test
    fun `should return combined list of all category events, tasks and notes with status code 200 OK`() {
        val id: UUID = sampleCategory.id
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
            category = sampleCategory
        )
        val task = Task(
            id = UUID.randomUUID(),
            title = "Sample Task",
            description = "Task description",
            status = TaskStatus.TODO,
            calendar = calendar,
            category = sampleCategory
        )
        val note = Note(
            id = UUID.randomUUID(),
            title = "Sample Note",
            description = "Note description",
            calendar = calendar,
            category = sampleCategory
        )

        whenever(eventService.getAllByCategoryId(id, pageable)).thenReturn(PageImpl(listOf(event)))
        whenever(taskService.getAllByCategoryId(id, pageable)).thenReturn(PageImpl(listOf(task)))
        whenever(noteService.getAllByCategoryId(id, pageable)).thenReturn(PageImpl(listOf(note)))

        val response: ResponseEntity<List<Map<String, Any>>> = categoryController.getAllItems(id, pageable)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(3, response.body?.size)

        val types: List<String> = response.body?.mapNotNull { it["type"] as? String } ?: emptyList()
        assertTrue(types.containsAll(listOf("event", "task", "note")))

        verify(eventService).getAllByCategoryId(id, pageable)
        verify(taskService).getAllByCategoryId(id, pageable)
        verify(noteService).getAllByCategoryId(id, pageable)
    }

    @Test
    fun `should return updated category with status code 200 OK`() {
        val updatedCategory: Category = sampleCategory.copy(title = "Work")
        whenever(categoryService.update(sampleCategory.id, sampleCategoryDto)).thenReturn(updatedCategory)

        val response: ResponseEntity<CategoryDto> = categoryController.update(sampleCategory.id, sampleCategoryDto)

        assertEquals(HttpStatus.OK, response.statusCode)
        assertEquals(updatedCategory.toDto(), response.body)

        verify(categoryService).update(sampleCategory.id, sampleCategoryDto)
    }

    @Test
    fun `should delete category with status code 204 No Content`() {
        doNothing().whenever(categoryService).delete(sampleCategory.id)

        val response: ResponseEntity<Void> = categoryController.delete(sampleCategory.id)

        assertEquals(HttpStatus.NO_CONTENT, response.statusCode)

        verify(categoryService).delete(sampleCategory.id)
    }

}
