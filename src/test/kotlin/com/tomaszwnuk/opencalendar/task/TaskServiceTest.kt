/*
 * Copyright (c) Tomasz Wnuk
 */

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

/**
 * Unit tests for the `TaskService` class.
 * Verifies the behavior of the service methods using mocked dependencies.
 */
@ExtendWith(MockitoExtension::class)
internal class TaskServiceTest {

    /**
     * Mocked instance of `TaskRepository` for simulating task-related database operations.
     */
    @Mock
    private lateinit var _taskRepository: TaskRepository

    /**
     * Mocked instance of `CalendarRepository` for simulating calendar-related database operations.
     */
    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    /**
     * Mocked instance of `CategoryRepository` for simulating category-related database operations.
     */
    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    /**
     * Instance of `TaskService` under test.
     */
    private lateinit var _service: TaskService

    /**
     * Sample `Calendar` instance used in tests.
     */
    private val sampleCalendar = Calendar(
        id = UUID.randomUUID(), title = "Development Calendar", emoji = "💻"
    )

    /**
     * Sample `Category` instance used in tests.
     */
    private val sampleCategory = Category(
        id = UUID.randomUUID(), title = "High Priority", color = "#FF4500"
    )

    /**
     * Sets up the test environment before each test.
     * Initializes the `TaskService` with mocked repositories.
     */
    @BeforeEach
    fun setUp() {
        _service = TaskService(_taskRepository, _calendarRepository, _categoryRepository)
    }

    /**
     * Tests the creation of a task.
     * Verifies that the service returns the created task with a generated ID.
     */
    @Test
    fun `should return created task`() {
        val dto = TaskDto(
            title = "Code Review",
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
        assertEquals("Code Review", result.title)
        assertEquals(TaskStatus.TODO, result.status)
        assertEquals(sampleCalendar.id, result.calendarId)
        assertEquals(sampleCategory.id, result.categoryId)

        verify(_calendarRepository).findById(sampleCalendar.id)
        verify(_categoryRepository).findById(sampleCategory.id)
        verify(_taskRepository).save(argThat { title == "Code Review" && status == TaskStatus.TODO })
    }

    /**
     * Tests the creation of a task with a missing calendar.
     * Verifies that the service throws a `NoSuchElementException`.
     */
    @Test
    fun `should throw error when creating task with missing calendar`() {
        val dto = TaskDto(
            title = "Deployment",
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

    /**
     * Tests retrieving a task by its ID.
     * Verifies that the service returns the correct task.
     */
    @Test
    fun `should return task by id`() {
        val id = UUID.randomUUID()
        val task = Task(
            id = id,
            title = "Sprint Planning",
            description = "Discuss upcoming sprint goals",
            status = TaskStatus.IN_PROGRESS,
            calendar = sampleCalendar,
            category = null
        )
        whenever(_taskRepository.findById(id)).thenReturn(Optional.of(task))

        val result = _service.getById(id = id)

        assertEquals(id, result.id)
        assertEquals("Sprint Planning", result.title)
        assertEquals(TaskStatus.IN_PROGRESS, result.status)

        verify(_taskRepository).findById(id)
    }

    /**
     * Tests retrieving a task by a non-existent ID.
     * Verifies that the service throws a `NoSuchElementException`.
     */
    @Test
    fun `should throw error when task id not found`() {
        val id = UUID.randomUUID()
        whenever(_taskRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.getById(id = id) }
        verify(_taskRepository).findById(id)
    }

    /**
     * Tests retrieving all tasks.
     * Verifies that the service returns a list of all tasks.
     */
    @Test
    fun `should return all tasks`() {
        val bugFix = Task(
            title = "Bug Fix",
            description = "Fix critical production bug",
            status = TaskStatus.TODO,
            calendar = sampleCalendar
        )
        val writeDocs = Task(
            title = "Write Documentation",
            description = "Document new API endpoints",
            status = TaskStatus.DONE,
            calendar = sampleCalendar
        )
        whenever(_taskRepository.findAll()).thenReturn(listOf(bugFix, writeDocs))

        val result = _service.getAll()
        assertEquals(2, result.size)
        assertTrue(result.any { it.title == "Bug Fix" })
        assertTrue(result.any { it.status == TaskStatus.DONE })
        verify(_taskRepository).findAll()
    }

    /**
     * Tests retrieving tasks by calendar ID.
     * Verifies that the service returns a list of tasks associated with the specified calendar.
     */
    @Test
    fun `should return tasks by calendar id`() {
        val calendarId = sampleCalendar.id
        val teamSync = Task(
            title = "Team Sync",
            description = "Weekly team stand-up",
            status = TaskStatus.TODO,
            calendar = sampleCalendar
        )
        whenever(_taskRepository.findAllByCalendarId(calendarId = calendarId)).thenReturn(listOf(teamSync))

        val result = _service.getAllByCalendarId(calendarId = calendarId)
        assertEquals(1, result.size)
        assertEquals("Team Sync", result[0].title)
        verify(_taskRepository).findAllByCalendarId(calendarId = calendarId)
    }

    /**
     * Tests retrieving tasks by category ID.
     * Verifies that the service returns a list of tasks associated with the specified category.
     */
    @Test
    fun `should return tasks by category id`() {
        val categoryId = sampleCategory.id
        val securityAudit = Task(
            title = "Security Audit",
            description = "Review security protocols",
            status = TaskStatus.TODO,
            calendar = sampleCalendar,
            category = sampleCategory
        )
        whenever(_taskRepository.findAllByCategoryId(categoryId = categoryId)).thenReturn(listOf(securityAudit))

        val result = _service.getAllByCategoryId(categoryId = categoryId)
        assertEquals(1, result.size)
        assertEquals("Security Audit", result[0].title)
        verify(_taskRepository).findAllByCategoryId(categoryId = categoryId)
    }

    /**
     * Tests filtering tasks based on criteria.
     * Verifies that the service returns a list of matching tasks.
     */
    @Test
    fun `should return filtered tasks`() {
        val filter = TaskFilterDto(
            title = "Release",
            description = null,
            status = TaskStatus.DONE,
            calendarId = null,
            categoryId = null
        )
        val releaseTask = Task(
            title = "Release v2.0",
            description = "Deploy version 2.0",
            status = TaskStatus.DONE,
            calendar = sampleCalendar
        )
        whenever(
            _taskRepository.filter(
                title = "Release",
                description = null,
                status = TaskStatus.DONE,
                calendarId = null,
                categoryId = null
            )
        ).thenReturn(listOf(releaseTask))

        val result = _service.filter(filter = filter)
        assertEquals(1, result.size)
        assertEquals("Release v2.0", result[0].title)
        verify(_taskRepository).filter(
            title = "Release",
            description = null,
            status = TaskStatus.DONE,
            calendarId = null,
            categoryId = null
        )
    }

    /**
     * Tests updating a task.
     * Verifies that the service updates the task and returns the updated entity.
     */
    @Test
    fun `should return updated task`() {
        val id = UUID.randomUUID()
        val existing = Task(
            id = id,
            title = "Draft Report",
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

    /**
     * Tests updating a non-existent task.
     * Verifies that the service throws a `NoSuchElementException`.
     */
    @Test
    fun `should throw error when updating non existing task`() {
        val id = UUID.randomUUID()
        val dto = TaskDto(
            id = null,
            title = "Ghost Task",
            description = null,
            status = TaskStatus.TODO,
            calendarId = sampleCalendar.id,
            categoryId = null
        )
        whenever(_taskRepository.findById(id)).thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.update(id = id, dto = dto) }
        verify(_taskRepository).findById(id)
    }

    /**
     * Tests deleting a task that exists.
     * Verifies that the service deletes the task.
     */
    @Test
    fun `should delete task when exists`() {
        val id = UUID.randomUUID()
        val cleanupTask = Task(
            title = "Log Cleanup",
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

    /**
     * Tests deleting all tasks by calendar ID.
     * Verifies that the service deletes all tasks associated with the specified calendar.
     */
    @Test
    fun `should delete all tasks by calendar id`() {
        val calendarId = sampleCalendar.id
        val databaseCleanup = Task(
            title = "Database Cleanup",
            description = "Remove outdated records",
            status = TaskStatus.TODO,
            calendar = sampleCalendar
        )
        val logArchiving = databaseCleanup.copy(
            id = UUID.randomUUID(),
            title = "Log Archiving",
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

    /**
     * Tests clearing the category for all tasks by category ID.
     * Verifies that the service removes the category association from all tasks in the specified category.
     */
    @Test
    fun `should clear category for all tasks by category id`() {
        val categoryId = sampleCategory.id
        val auditTask = Task(
            title = "Security Audit",
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
