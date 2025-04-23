package com.tomaszwnuk.opencalendar.note

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.category.CategoryColors
import com.tomaszwnuk.opencalendar.category.CategoryRepository
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
import java.awt.Color
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NoteServiceTest {

    @Mock
    private lateinit var _noteRepository: NoteRepository

    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var _noteService: NoteService

    private lateinit var _sampleCalendar: Calendar

    private lateinit var _sampleCategory: Category

    private lateinit var _sampleNote: Note

    private lateinit var _sampleDto: NoteDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            name = "Personal",
            emoji = "\\uD83C\\uDFE0"
        )
        _sampleCategory = Category(
            id = UUID.randomUUID(),
            name = "Shopping",
            color = CategoryColors.toHex(Color.YELLOW)
        )
        _sampleNote = Note(
            id = UUID.randomUUID(),
            name = "Groceries",
            description = "Buy milk, eggs, and bread",
            calendar = _sampleCalendar,
            category = _sampleCategory
        )
        _sampleDto = _sampleNote.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created note`() {
        whenever(_calendarRepository.findById(_sampleDto.calendarId)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(_sampleNote).whenever(_noteRepository).save(any())
        val result: Note = _noteService.create(_sampleDto)

        assertEquals(_sampleNote.id, result.id)
        verify(_noteRepository).save(any())
    }

    @Test
    fun `should return paginated list of notes`() {
        val notes: List<Note> = listOf(_sampleNote, _sampleNote, _sampleNote)

        whenever(_noteRepository.findAll(_pageable)).thenReturn(PageImpl(notes))
        val result: Page<Note> = _noteService.getAll(_pageable)

        assertEquals(notes.size, result.totalElements.toInt())
        verify(_noteRepository).findAll(_pageable)
    }

    @Test
    fun `should return note by id`() {
        val id: UUID = _sampleNote.id

        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(_sampleNote))
        val result: Note = _noteService.getById(id)

        assertEquals(_sampleNote.name, result.name)
        verify(_noteRepository).findById(id)
    }

    @Test
    fun `should return filtered notes`() {
        val filter = NoteFilterDto(name = "Groceries")
        val notes: List<Note> = listOf(_sampleNote, _sampleNote, _sampleNote)

        whenever(
            _noteRepository.filter(
                eq(filter.name),
                isNull(),
                isNull(),
                isNull(),
                eq(_pageable)
            )
        ).thenReturn(PageImpl(notes))
        val result: Page<Note> = _noteService.filter(filter, _pageable)

        assertEquals(notes.size, result.totalElements.toInt())
        verify(_noteRepository).filter(
            eq(filter.name),
            isNull(),
            isNull(),
            isNull(),
            eq(_pageable)
        )
    }

    @Test
    fun `should return updated note`() {
        val id: UUID = _sampleNote.id
        val updated: Note = _sampleNote.copy(name = "Updated note")

        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(_sampleNote))
        whenever(_calendarRepository.findById(_sampleDto.calendarId)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_categoryRepository.findById(_sampleDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(updated).whenever(_noteRepository).save(any())
        val result: Note = _noteService.update(id, _sampleDto)

        assertEquals(updated.name, result.name)
        verify(_noteRepository).save(any())
    }

    @Test
    fun `should delete note`() {
        val id: UUID = _sampleNote.id

        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(_sampleNote))
        doNothing().whenever(_noteRepository).delete(_sampleNote)
        _noteService.delete(id)

        verify(_noteRepository).delete(_sampleNote)
    }

}
