package com.ozzz7y.opencalendar.note

import com.ozzz7y.opencalendar.domain.calendar.Calendar
import com.ozzz7y.opencalendar.domain.calendar.CalendarRepository
import com.ozzz7y.opencalendar.domain.category.Category
import com.ozzz7y.opencalendar.domain.category.CategoryRepository
import com.ozzz7y.opencalendar.domain.note.*
import com.ozzz7y.opencalendar.domain.user.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
internal class NoteServiceTest {

    @Mock private lateinit var _noteRepository: NoteRepository
    @Mock private lateinit var _calendarRepository: CalendarRepository
    @Mock private lateinit var _categoryRepository: CategoryRepository
    @Mock private lateinit var _userService: UserService

    private lateinit var _service: NoteService
    private lateinit var _userId: UUID
    private lateinit var _calendar: Calendar
    private lateinit var _category: Category
    private lateinit var _note: Note
    private lateinit var _noteList: List<Note>

    @BeforeEach
    fun setUp() {
        _service   = NoteService(_noteRepository, _calendarRepository, _categoryRepository, _userService)
        _userId    = UUID.randomUUID()

        _calendar  = Calendar(UUID.randomUUID(), "Project Calendar", "📅", _userId)
        _category  = Category(UUID.randomUUID(), "Announcements", "#FFA500", _userId)
        _note      = Note(UUID.randomUUID(), "Kick-off", "Meeting at 10", _calendar, _category)
        _noteList  = listOf(_note, _note.copy(), _note.copy())
    }

    @Test
    fun `should return created note`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_calendarRepository.findByIdAndUserId(_calendar.id, _userId))
            .thenReturn(Optional.of(_calendar))
        whenever(_categoryRepository.findByIdAndUserId(_category.id, _userId))
            .thenReturn(Optional.of(_category))
        whenever(_noteRepository.save(any<Note>())).thenReturn(_note)

        val result = _service.create(_note.toDto())

        assertEquals(_note.toDto(), result)
        verify(_noteRepository).save(any())
    }

    @Test
    fun `should throw error when creating note with missing calendar`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_calendarRepository.findByIdAndUserId(_calendar.id, _userId))
            .thenReturn(Optional.empty())

        assertThrows<IllegalArgumentException> { _service.create(_note.toDto()) }
        verify(_noteRepository, never()).save(any())
    }

    @Test
    fun `should return note by id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_noteRepository.findByIdAndCalendarUserId(_note.id, _userId))
            .thenReturn(Optional.of(_note))

        val result = _service.getById(_note.id)

        assertEquals(_note.toDto(), result)
        verify(_noteRepository).findByIdAndCalendarUserId(_note.id, _userId)
    }

    @Test
    fun `should throw error when note id not found`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_noteRepository.findByIdAndCalendarUserId(_note.id, _userId))
            .thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.getById(_note.id) }
        verify(_noteRepository).findByIdAndCalendarUserId(_note.id, _userId)
    }

    @Test
    fun `should return all notes`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_noteRepository.findAllByCalendarUserId(_userId)).thenReturn(_noteList)

        val result = _service.getAll()

        assertEquals(_noteList.map { it.toDto() }, result)
        verify(_noteRepository).findAllByCalendarUserId(_userId)
    }

    @Test
    fun `should return all notes by calendar id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_noteRepository.findAllByCalendarIdAndUserId(_calendar.id, _userId))
            .thenReturn(_noteList)

        val result = _service.getAllByCalendarId(_calendar.id)

        assertEquals(_noteList.map { it.toDto() }, result)
        verify(_noteRepository).findAllByCalendarIdAndUserId(_calendar.id, _userId)
    }

    @Test
    fun `should return all notes by category id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_noteRepository.findAllByCategoryIdAndUserId(_category.id, _userId))
            .thenReturn(_noteList)

        val result = _service.getAllByCategoryId(_category.id)

        assertEquals(_noteList.map { it.toDto() }, result)
        verify(_noteRepository).findAllByCategoryIdAndUserId(_category.id, _userId)
    }

    @Test
    fun `should return filtered notes`() {
        val filter = NoteFilterDto(
            name = "Kick-off", description = null,
            calendarId = null, categoryId = null
        )

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(
            _noteRepository.filter(
                userId = _userId,
                name = filter.name,
                description = filter.description,
                calendarId = filter.calendarId,
                categoryId = filter.categoryId
            )
        ).thenReturn(_noteList)

        val result = _service.filter(filter)

        assertEquals(_noteList.map { it.toDto() }, result)
        verify(_noteRepository).filter(
            userId = _userId,
            name = filter.name,
            description = filter.description,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId
        )
    }

    @Test
    fun `should return updated note`() {
        val dto = _note.toDto().copy(name = "Updated")

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_noteRepository.findByIdAndCalendarUserId(_note.id, _userId))
            .thenReturn(Optional.of(_note))
        whenever(_calendarRepository.findByIdAndUserId(dto.calendarId, _userId))
            .thenReturn(Optional.of(_calendar))
        whenever(_categoryRepository.findByIdAndUserId(dto.categoryId!!, _userId))
            .thenReturn(Optional.of(_category))
        whenever(_noteRepository.save(any<Note>())).thenAnswer { it.arguments[0] as Note }

        val result = _service.update(_note.id, dto)

        assertEquals(dto, result)
        verify(_noteRepository).save(any())
    }

    @Test
    fun `should throw error when updating non existing note`() {
        val dto = _note.toDto()

        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_noteRepository.findByIdAndCalendarUserId(_note.id, _userId))
            .thenReturn(Optional.empty())

        assertThrows<NoSuchElementException> { _service.update(_note.id, dto) }
        verify(_noteRepository, never()).save(any())
    }

    @Test
    fun `should delete note`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_noteRepository.findByIdAndCalendarUserId(_note.id, _userId))
            .thenReturn(Optional.of(_note))
        doNothing().whenever(_noteRepository).delete(_note)

        _service.delete(_note.id)

        verify(_noteRepository).findByIdAndCalendarUserId(_note.id, _userId)
        verify(_noteRepository).delete(_note)
    }

    @Test
    fun `should delete all notes by calendar id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_noteRepository.findAllByCalendarIdAndUserId(_calendar.id, _userId))
            .thenReturn(_noteList)
        doNothing().whenever(_noteRepository).deleteAll(_noteList)

        _service.deleteByCalendarId(_calendar.id)

        verify(_noteRepository).findAllByCalendarIdAndUserId(_calendar.id, _userId)
        verify(_noteRepository).deleteAll(_noteList)
    }

    @Test
    fun `should clear category for all notes by category id`() {
        whenever(_userService.getCurrentUserId()).thenReturn(_userId)
        whenever(_noteRepository.findAllByCategoryIdAndUserId(_category.id, _userId))
            .thenReturn(_noteList)
        whenever(_noteRepository.save(any<Note>())).thenAnswer { it.arguments[0] as Note }

        _service.removeCategoryByCategoryId(_category.id)

        verify(_noteRepository).findAllByCategoryIdAndUserId(_category.id, _userId)
        verify(_noteRepository, times(_noteList.size)).save(argThat { this.category == null })
    }
}
