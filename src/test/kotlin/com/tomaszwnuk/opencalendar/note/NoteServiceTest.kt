//package com.tomaszwnuk.opencalendar.note
//
//import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
//import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
//import com.tomaszwnuk.opencalendar.domain.category.Category
//import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
//import com.tomaszwnuk.opencalendar.domain.note.*
//import org.junit.jupiter.api.Assertions.*
//import org.junit.jupiter.api.BeforeEach
//import org.junit.jupiter.api.Test
//import org.junit.jupiter.api.assertThrows
//import org.junit.jupiter.api.extension.ExtendWith
//import org.mockito.Mock
//import org.mockito.junit.jupiter.MockitoExtension
//import org.mockito.kotlin.*
//import java.util.*
//
//@ExtendWith(MockitoExtension::class)
//internal class NoteServiceTest {
//
//    @Mock
//    private lateinit var _noteRepository: NoteRepository
//
//    @Mock
//    private lateinit var _calendarRepository: CalendarRepository
//
//    @Mock
//    private lateinit var _categoryRepository: CategoryRepository
//
//    private lateinit var _service: NoteService
//
//    private val sampleCalendar = Calendar(
//        id = UUID.randomUUID(), name = "Project Calendar", emoji = "📅"
//    )
//
//    private val sampleCategory = Category(
//        id = UUID.randomUUID(), name = "Announcements", color = "#FFA500"
//    )
//
//    @BeforeEach
//    fun setUp() {
//        _service = NoteService(_noteRepository, _calendarRepository, _categoryRepository)
//    }
//
//    @Test
//    fun `should return created note`() {
//        val dto = NoteDto(
//            name = "Weekly Update",
//            description = "Team progress overview",
//            calendarId = sampleCalendar.id,
//            categoryId = sampleCategory.id
//        )
//        val savedId: UUID = UUID.randomUUID()
//
//        whenever(_calendarRepository.findById(sampleCalendar.id)).thenReturn(Optional.of(sampleCalendar))
//        whenever(_categoryRepository.findById(sampleCategory.id)).thenReturn(Optional.of(sampleCategory))
//        whenever(_noteRepository.save(any<Note>())).thenAnswer { invocation ->
//            val arg: Note = invocation.getArgument(0)
//            arg.copy(id = savedId)
//        }
//
//        val result = _service.create(dto = dto)
//
//        assertNotNull(result.id)
//        assertEquals(savedId, result.id)
//        assertEquals("Weekly Update", result.name)
//        assertEquals("Team progress overview", result.description)
//        assertEquals(sampleCalendar.id, result.calendarId)
//        assertEquals(sampleCategory.id, result.categoryId)
//
//        verify(_calendarRepository).findById(sampleCalendar.id)
//        verify(_categoryRepository).findById(sampleCategory.id)
//        verify(_noteRepository).save(argThat { name == "Weekly Update" && description == "Team progress overview" })
//    }
//
//    @Test
//    fun `should throw error when creating note with missing calendar`() {
//        val dto = NoteDto(
//            name = "Missing Calendar",
//            description = "No calendar available",
//            calendarId = UUID.randomUUID(),
//            categoryId = null
//        )
//        whenever(_calendarRepository.findById(dto.calendarId)).thenReturn(Optional.empty())
//
//        assertThrows<NoSuchElementException> {
//            _service.create(dto = dto)
//        }
//
//        verify(_calendarRepository).findById(dto.calendarId)
//        verify(_noteRepository, never()).save(any<Note>())
//    }
//
//    @Test
//    fun `should return note by id`() {
//        val id = UUID.randomUUID()
//        val note = Note(
//            id = id,
//            name = "Memo",
//            description = "Keep this in mind",
//            calendar = sampleCalendar,
//            category = null
//        )
//        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(note))
//
//        val result = _service.getById(id = id)
//
//        assertEquals(id, result.id)
//        assertEquals("Memo", result.name)
//        assertEquals("Keep this in mind", result.description)
//
//        verify(_noteRepository).findById(id)
//    }
//
//    @Test
//    fun `should throw error when note id not found`() {
//        val id = UUID.randomUUID()
//        whenever(_noteRepository.findById(id)).thenReturn(Optional.empty())
//
//        assertThrows<NoSuchElementException> {
//            _service.getById(id = id)
//        }
//
//        verify(_noteRepository).findById(id)
//    }
//
//    @Test
//    fun `should return all notes by calendar id`() {
//        val note1 = Note(
//            name = "Standup Notes", description = "Daily standup summary",
//            calendar = sampleCalendar
//        )
//        val note2 = Note(
//            name = "Planning Notes", description = "Sprint planning details",
//            calendar = sampleCalendar
//        )
//        whenever(_noteRepository.findAllByCalendarId(calendarId = sampleCalendar.id)).thenReturn(listOf(note1, note2))
//
//        val result = _service.getAllByCalendarId(calendarId = sampleCalendar.id)
//
//        assertEquals(2, result.size)
//        assertTrue(result.any { it.name == "Standup Notes" })
//        assertTrue(result.any { it.name == "Planning Notes" })
//
//        verify(_noteRepository).findAllByCalendarId(calendarId = sampleCalendar.id)
//    }
//
//    @Test
//    fun `should return all notes by category id`() {
//        val note = Note(
//            name = "Announcement", description = "New policy",
//            calendar = sampleCalendar,
//            category = sampleCategory
//        )
//        whenever(_noteRepository.findAllByCategoryId(categoryId = sampleCategory.id)).thenReturn(listOf(note))
//
//        val result = _service.getAllByCategoryId(categoryId = sampleCategory.id)
//
//        assertEquals(1, result.size)
//        assertEquals("Announcement", result[0].name)
//
//        verify(_noteRepository).findAllByCategoryId(categoryId = sampleCategory.id)
//    }
//
//    @Test
//    fun `should return all notes`() {
//        val note01 = Note(
//            name = "Note One", description = "Description one",
//            calendar = sampleCalendar
//        )
//        val note02 = Note(
//            name = "Note Two", description = "Description two",
//            calendar = sampleCalendar
//        )
//        whenever(_noteRepository.findAll()).thenReturn(listOf(note01, note02))
//
//        val result = _service.getAll()
//
//        assertEquals(2, result.size)
//        assertTrue(result.any { it.name == "Note One" })
//        assertTrue(result.any { it.name == "Note Two" })
//
//        verify(_noteRepository).findAll()
//    }
//
//    @Test
//    fun `should return filtered notes`() {
//        val filter = NoteFilterDto(
//            name = "Standup", description = null,
//            calendarId = null, categoryId = null
//        )
//        val standupNote = Note(
//            name = "Standup Notes", description = "Morning updates",
//            calendar = sampleCalendar
//        )
//        whenever(
//            _noteRepository.filter(
//                name = "Standup",
//                description = null,
//                calendarId = null,
//                categoryId = null
//            )
//        ).thenReturn(listOf(standupNote))
//
//        val result = _service.filter(filter = filter)
//
//        assertEquals(1, result.size)
//        assertEquals("Standup Notes", result[0].name)
//
//        verify(_noteRepository).filter(name = "Standup", description = null, calendarId = null, categoryId = null)
//    }
//
//    @Test
//    fun `should return updated note`() {
//        val id = UUID.randomUUID()
//        val existing = Note(
//            id = id,
//            name = "Draft",
//            description = "Initial draft",
//            calendar = sampleCalendar,
//            category = sampleCategory
//        )
//        val dto = existing.toDto().copy(name = "Final Draft", description = "Updated draft")
//        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(existing))
//        whenever(_calendarRepository.findById(dto.calendarId)).thenReturn(Optional.of(sampleCalendar))
//        whenever(_categoryRepository.findById(dto.categoryId!!)).thenReturn(Optional.of(sampleCategory))
//        whenever(_noteRepository.save(any<Note>())).thenAnswer { it.getArgument<Note>(0) }
//
//        val result = _service.update(id = id, dto = dto)
//
//        assertEquals("Final Draft", result.name)
//        assertEquals("Updated draft", result.description)
//        verify(_noteRepository).findById(id)
//        verify(_noteRepository).save(argThat { name == "Final Draft" && description == "Updated draft" })
//    }
//
//    @Test
//    fun `should throw error when updating non existing note`() {
//        val id = UUID.randomUUID()
//        val dto = NoteDto(
//            id = null,
//            name = "Ghost Note",
//            description = "Does not exist",
//            calendarId = sampleCalendar.id,
//            categoryId = null
//        )
//        whenever(_noteRepository.findById(id)).thenReturn(Optional.empty())
//
//        assertThrows<NoSuchElementException> {
//            _service.update(id = id, dto = dto)
//        }
//
//        verify(_noteRepository).findById(id)
//    }
//
//    @Test
//    fun `should delete note when exists`() {
//        val id = UUID.randomUUID()
//        val existing = Note(
//            name = "Reminder",
//            description = "Pay bills",
//            calendar = sampleCalendar
//        )
//        whenever(_noteRepository.findById(id)).thenReturn(Optional.of(existing))
//        doNothing().whenever(_noteRepository).delete(existing)
//
//        _service.delete(id = id)
//
//        verify(_noteRepository).findById(id)
//        verify(_noteRepository).delete(existing)
//    }
//
//    @Test
//    fun `should delete all notes by calendar id`() {
//        val calendarId = sampleCalendar.id
//        val noteA = Note(
//            name = "Title", description = "Description", calendar = sampleCalendar
//        )
//        val noteB = noteA.copy(id = UUID.randomUUID())
//        whenever(_noteRepository.findAllByCalendarId(calendarId = calendarId)).thenReturn(listOf(noteA, noteB))
//        doNothing().whenever(_noteRepository).deleteAll(listOf(noteA, noteB))
//
//        _service.deleteByCalendarId(calendarId = calendarId)
//
//        verify(_noteRepository).findAllByCalendarId(calendarId = calendarId)
//        verify(_noteRepository).deleteAll(listOf(noteA, noteB))
//    }
//
//    @Test
//    fun `should clear category for all notes by category id`() {
//        val categoryId = sampleCategory.id
//        val note01 = Note(
//            name = "Note", description = "Description",
//            calendar = sampleCalendar, category = sampleCategory
//        )
//        val note02 = note01.copy(id = UUID.randomUUID())
//        whenever(_noteRepository.findAllByCategoryId(categoryId = categoryId)).thenReturn(listOf(note01, note02))
//        whenever(_noteRepository.save(any<Note>())).thenAnswer { it.getArgument<Note>(0) }
//
//        _service.removeCategoryByCategoryId(categoryId = categoryId)
//
//        verify(_noteRepository).findAllByCategoryId(categoryId = categoryId)
//        verify(_noteRepository).save(argThat { id == note01.id && category == null })
//        verify(_noteRepository).save(argThat { id == note02.id && category == null })
//    }
//
//}
