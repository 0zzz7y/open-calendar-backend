package com.tomaszwnuk.opencalendar.task

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.task.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class TaskServiceTest {

    @Mock
    private lateinit var _taskRepository: TaskRepository

    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    private lateinit var _service: TaskService

    private val sampleCalendar = Calendar(
        id = UUID.randomUUID(), name = "Development Calendar", emoji = "💻"
    )

    private val sampleCategory = Category(
        id = UUID.randomUUID(), name = "High Priority", color = "#FF4500"
    )

    @BeforeEach
    fun setUp() {
        _service = TaskService(_taskRepository, _calendarRepository, _categoryRepository)
    }

    @Test
    fun `should return created task`() {
        val dto = TaskDto(
            name = "Code Review",
            description = "Review team's pull requests",
            status = TaskStatus.TODO,
            calendarId = sampleCalendar.id,
            categoryId = sampleCategory.id
        )
        val savedId = UUID.randomUUID()

        whenever(_calendarRepository.findById(sampleCalendar.id)).thenReturn(Optional.of(sampleCalendar))
        whenever(_categoryRepository.findById(sampleCategory.id)).thenReturn(Optional.of(sampleCategory))
        whenever(_taskRepository.save(any<Task>())).thenAnswer { invocation ->
            val arg: Task = invocation.getArgument(0)
            arg.copy(id = savedId)
        }

        val result = _service.create(dto = dto)

        assertNotNull(result.id)
        assertEquals(savedId, result.id)
        assertEquals("Code Review", result.name)
        assertEquals(TaskStatus.TODO, result.status)
        assertEquals(sampleCalendar.id, result.calendarId)
        assertEquals(sampleCategory.id, result.categoryId)

        verify(_calendarRepository).findById(sampleCalendar.id)
        verify(_categoryRepository).findById(sampleCategory.id)
        verify(_taskRepository).save(argThat { name == "Code Review" && status == TaskStatus.TODO })
    }

    @Test
    fun `should throw error when creating task with missing calendar`() {
        val dto = TaskDto(
            name = "Deployment",
            description = "Deploy new release",
            status = TaskStatus.TODO,
            calendarId = UUID.randomUUID(),
            categoryId = null
        )
        whenever(_calendarRepository.findById(dto.calendarId)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.create(dto = dto) }

        verify(_calendarRepository).findById(dto.calendarId)
        verify(_taskRepository, never()).save(any<Task>())
    }

    @Test
    fun `should return task by id`() {
        val id = UUID.randomUUID()
        val task = Task(
            id = id,
            name = "Sprint Planning",
            description = "Discuss upcoming sprint goals",
            status = TaskStatus.IN_PROGRESS,
            calendar = sampleCalendar,
            category = null
        )
        whenever(_taskRepository.findById(id)).thenReturn(Optional.of(task))

        val result = _service.getById(id = id)

        assertEquals(id, result.id)
        assertEquals("Sprint Planning", result.name)
        assertEquals(TaskStatus.IN_PROGRESS, result.status)

        verify(_taskRepository).findById(id)
    }

    @Test
    fun `should throw error when task id not found`() {
        val id = UUID.randomUUID()
        whenever(_taskRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.getById(id = id) }
        verify(_taskRepository).findById(id)
    }

    @Test
    fun `should return all tasks`() {
        val bugFix = Task(
            name = "Bug Fix",
            description = "Fix critical production bug",
            status = TaskStatus.TODO,
            calendar = sampleCalendar
        )
        val writeDocs = Task(
            name = "Write Documentation",
            description = "Document new API endpoints",
            status = TaskStatus.DONE,
            calendar = sampleCalendar
        )
        whenever(_taskRepository.findAll()).thenReturn(listOf(bugFix, writeDocs))

        val result = _service.getAll()
        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Bug Fix" })
        assertTrue(result.any { it.status == TaskStatus.DONE })
        verify(_taskRepository).findAll()
    }

    @Test
    fun `should return tasks by calendar id`() {
        val calendarId = sampleCalendar.id
        val teamSync = Task(
            name = "Team Sync",
            description = "Weekly team stand-up",
            status = TaskStatus.TODO,
            calendar = sampleCalendar
        )
        whenever(_taskRepository.findAllByCalendarId(calendarId = calendarId)).thenReturn(listOf(teamSync))

        val result = _service.getAllByCalendarId(calendarId = calendarId)
        assertEquals(1, result.size)
        assertEquals("Team Sync", result[0].name)
        verify(_taskRepository).findAllByCalendarId(calendarId = calendarId)
    }

    @Test
    fun `should return tasks by category id`() {
        val categoryId = sampleCategory.id
        val securityAudit = Task(
            name = "Security Audit",
            description = "Review security protocols",
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        whenever(_taskRepository.findAllByCategoryId(categoryId = categoryId)).thenReturn(listOf(securityAudit))

        val result = _service.getAllByCategoryId(categoryId = categoryId)
        assertEquals(1, result.size)
        assertEquals("Security Audit", result[0].name)
        verify(_taskRepository).findAllByCategoryId(categoryId = categoryId)
    }

    @Test
    fun `should return filtered tasks`() {
        val filter = TaskFilterDto(
            name = "Release",
            description = null,
            status = TaskStatus.DONE,
            calendarId = null,
            categoryId = null
        )
        val releaseTask = Task(
            name = "Release v2.0",
            description = "Deploy version 2.0",
            status = TaskStatus.DONE,
            calendar = sampleCalendar
        )
        whenever(
            _taskRepository.filter(
                name = "Release",
                description = null,
                status = TaskStatus.DONE,
                calendarId = null,
                categoryId = null
            )
        ).thenReturn(listOf(releaseTask))

        val result = _service.filter(filter = filter)
        assertEquals(1, result.size)
        assertEquals("Release v2.0", result[0].name)
        verify(_taskRepository).filter(
            name = "Release",
            description = null,
            status = TaskStatus.DONE,
            calendarId = null,
            categoryId = null
        )
    }

    @Test
    fun `should return updated task`() {
        val id = UUID.randomUUID()
        val existing = Task(
            id = id,
            name = "Draft Report",
            description = "Compile initial draft report",
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        val dto = existing.toDto().copy(status = TaskStatus.IN_PROGRESS)
        whenever(_taskRepository.findById(id)).thenReturn(Optional.of(existing))
        whenever(_calendarRepository.findById(dto.calendarId)).thenReturn(Optional.of(sampleCalendar))
        whenever(_categoryRepository.findById(dto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
        whenever(_taskRepository.save(any<Task>())).thenAnswer { it.getArgument<Task>(0) }

        val result = _service.update(id = id, dto = dto)
        assertEquals(TaskStatus.IN_PROGRESS, result.status)
        verify(_taskRepository).findById(id)
        verify(_taskRepository).save(argThat { status == TaskStatus.IN_PROGRESS })
    }

    @Test
    fun `should throw error when updating non existing task`() {
        val id = UUID.randomUUID()
        val dto = TaskDto(
            id = null,
            name = "Ghost Task",
            description = null,
            status = TaskStatus.TODO,
            calendarId = sampleCalendar.id,
            categoryId = null
        )
        whenever(_taskRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.update(id = id, dto = dto) }
        verify(_taskRepository).findById(id)
    }

    @Test
    fun `should delete task when exists`() {
        val id = UUID.randomUUID()
        val cleanupTask = Task(
            name = "Log Cleanup",
            description = "Clean up old logs",
            status = TaskStatus.TODO,
            calendar = sampleCalendar
        )
        whenever(_taskRepository.findById(id)).thenReturn(Optional.of(cleanupTask))
        doNothing().whenever(_taskRepository).delete(cleanupTask)

        _service.delete(id)
        verify(_taskRepository).findById(id)
        verify(_taskRepository).delete(cleanupTask)
    }

    @Test
    fun `should delete all tasks by calendar id`() {
        val calendarId = sampleCalendar.id
        val databaseCleanup = Task(
            name = "Database Cleanup",
            description = "Remove outdated records",
            status = TaskStatus.TODO,
            calendar = sampleCalendar
        )
        val logArchiving = databaseCleanup.copy(
            id = UUID.randomUUID(),
            name = "Log Archiving",
            description = "Archive system logs"
        )
        whenever(_taskRepository.findAllByCalendarId(calendarId = calendarId)).thenReturn(
            listOf(
                databaseCleanup,
                logArchiving
            )
        )
        doNothing().whenever(_taskRepository).deleteAll(listOf(databaseCleanup, logArchiving))

        _service.deleteAllByCalendarId(calendarId = calendarId)
        verify(_taskRepository).findAllByCalendarId(calendarId = calendarId)
        verify(_taskRepository).deleteAll(listOf(databaseCleanup, logArchiving))
    }

    @Test
    fun `should clear category for all tasks by category id`() {
        val categoryId = sampleCategory.id
        val auditTask = Task(
            name = "Security Audit",
            description = "Review security compliance",
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        val complianceFollowUp = auditTask.copy(id = UUID.randomUUID())
        whenever(_taskRepository.findAllByCategoryId(categoryId = categoryId)).thenReturn(
            listOf(
                auditTask,
                complianceFollowUp
            )
        )
        whenever(_taskRepository.save(any<Task>())).thenAnswer { it.getArgument<Task>(0) }

        _service.removeCategoryByCategoryId(categoryId = categoryId)
        verify(_taskRepository).findAllByCategoryId(categoryId = categoryId)
        verify(_taskRepository).save(argThat { id == auditTask.id && category == null })
        verify(_taskRepository).save(argThat { id == complianceFollowUp.id && category == null })
    }

}
