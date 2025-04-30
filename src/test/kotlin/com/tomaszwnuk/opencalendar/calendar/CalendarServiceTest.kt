package com.tomaszwnuk.opencalendar.calendar

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.opencalendar.domain.calendar.*
import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.*
import org.mockito.quality.Strictness
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CalendarServiceTest {

    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    @InjectMocks
    private lateinit var _calendarService: CalendarService

    private lateinit var _sampleCalendar: Calendar

    private lateinit var _sampleCalendarDto: CalendarDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\uD83C\uDFE0"
        )
        _sampleCalendarDto = _sampleCalendar.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should create and return calendar`() {
        val id: UUID = _sampleCalendar.id
        val title: String = _sampleCalendar.title
        val emoji: String = _sampleCalendar.emoji

        whenever(_calendarRepository.existsByTitle(title)).thenReturn(false)
        doReturn(_sampleCalendar).whenever(_calendarRepository).save(any())

        val result: CalendarDto = _calendarService.create(_sampleCalendarDto)

        assertNotNull(result)
        assertEquals(id, result.id)
        assertEquals(title, result.title)
        assertEquals(emoji, result.emoji)

        verify(_calendarRepository).save(any())
    }

    @Test
    fun `should return paginated list of all calendars`() {
        val calendars: List<Calendar> = listOf(
            _sampleCalendar,
            _sampleCalendar.copy(id = UUID.randomUUID()),
            _sampleCalendar.copy(id = UUID.randomUUID())
        )
        whenever(_calendarRepository.findAll(_pageable)).thenReturn(PageImpl(calendars))

        val result: List<CalendarDto> = _calendarService.getAll()

        assertEquals(calendars.size, result.size)
        assertEquals(calendars.map { it.id }, result.map { it.id })
        assertEquals(calendars.map { it.title }, result.map { it.title })

        verify(_calendarRepository).findAll(_pageable)
    }

    @Test
    fun `should return calendar by id`() {
        val id: UUID = _sampleCalendar.id
        whenever(_calendarRepository.findById(id)).thenReturn(Optional.of(_sampleCalendar))

        val result: CalendarDto = _calendarService.getById(id)

        assertNotNull(result)
        assertEquals(id, result.id)
        assertEquals(_sampleCalendar.title, result.title)
        assertEquals(_sampleCalendar.emoji, result.emoji)

        verify(_calendarRepository).findById(id)
    }

    @Test
    fun `should return paginated list of filtered calendars`() {
        val filter = CalendarFilterDto(title = "Personal")
        val calendars: List<Calendar> = listOf(
            _sampleCalendar,
            _sampleCalendar.copy(id = UUID.randomUUID()),
            _sampleCalendar.copy(id = UUID.randomUUID())
        )
        whenever(_calendarRepository.filter(eq(filter.title), eq(filter.emoji)))
            .thenReturn(calendars)

        val result: List<CalendarDto> = _calendarService.filter(filter)

        assertEquals(calendars.size, result.size)
        assertEquals(calendars.map { it.title }, result.map { it.title })

        verify(_calendarRepository).filter(eq(filter.title), eq(filter.emoji))
    }

    @Test
    fun `should update and return updated calendar`() {
        val id: UUID = _sampleCalendar.id
        val updatedTitle = "Work"
        val updatedCalendar: Calendar = _sampleCalendar.copy(title = updatedTitle)

        whenever(_calendarRepository.findById(id)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_calendarRepository.existsByTitle(updatedTitle)).thenReturn(false)
        doReturn(updatedCalendar).whenever(_calendarRepository).save(any())

        val result: CalendarDto = _calendarService.update(id, updatedCalendar.toDto())

        assertNotNull(result)
        assertEquals(updatedCalendar.id, result.id)
        assertEquals(updatedTitle, result.title)
        assertEquals(updatedCalendar.emoji, result.emoji)

        verify(_calendarRepository).save(any())
    }

    @Test
    fun `should delete calendar by id`() {
        val id: UUID = _sampleCalendar.id
        whenever(_calendarRepository.findById(id)).thenReturn(Optional.of(_sampleCalendar))
        doNothing().whenever(_calendarRepository).delete(_sampleCalendar)

        _calendarService.delete(id)

        verify(_calendarRepository).delete(_sampleCalendar)
    }

}
