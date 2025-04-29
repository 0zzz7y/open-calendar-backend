package com.tomaszwnuk.opencalendar.calendar

import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.opencalendar.TestConstants.PAGEABLE_PAGE_SIZE
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
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CalendarServiceTest {

    @Mock
    private lateinit var _calendarRepository: CalendarRepository

    @InjectMocks
    private lateinit var _calendarService: CalendarService

    private lateinit var _sampleCalendar: Calendar

    private lateinit var _sampleDto: CalendarDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\uD83C\uDFE0"
        )
        _sampleDto = _sampleCalendar.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should create and return calendar`() {
        whenever(_calendarRepository.existsByTitle(_sampleCalendar.title)).thenReturn(false)
        doReturn(_sampleCalendar).whenever(_calendarRepository).save(any())
        val result: Calendar = _calendarService.create(_sampleDto)

        assertEquals(_sampleCalendar.title, result.title)
        assertEquals(_sampleCalendar.emoji, result.emoji)
        verify(_calendarRepository).save(any())
    }

    @Test
    fun `should return paginated list of calendars`() {
        val calendars: List<Calendar> = listOf(
            _sampleCalendar,
            _sampleCalendar.copy(id = UUID.randomUUID()),
            _sampleCalendar.copy(id = UUID.randomUUID())
        )

        whenever(_calendarRepository.findAll(_pageable)).thenReturn(PageImpl(calendars))
        val result: Page<Calendar> = _calendarService.getAll(_pageable)

        assertEquals(calendars.size, result.totalElements.toInt())
        assertEquals(calendars.map { it.id }, result.content.map { it.id })
        verify(_calendarRepository).findAll(_pageable)
    }

    @Test
    fun `should return calendar by id`() {
        val id: UUID = _sampleCalendar.id

        whenever(_calendarRepository.findById(id)).thenReturn(Optional.of(_sampleCalendar))
        val result: Calendar = _calendarService.getById(id)

        assertEquals(_sampleCalendar.id, result.id)
        assertEquals(_sampleCalendar.title, result.title)
        verify(_calendarRepository).findById(id)
    }

    @Test
    fun `should return filtered calendars`() {
        val filter = CalendarFilterDto(title = "Personal")
        val calendars: List<Calendar> = listOf(
            _sampleCalendar,
            _sampleCalendar.copy(id = UUID.randomUUID()),
            _sampleCalendar.copy(id = UUID.randomUUID())
        )

        whenever(
            _calendarRepository.filter(
                eq(filter.title),
                eq(filter.emoji),
                eq(_pageable)
            )
        ).thenReturn(PageImpl(calendars))
        val result: Page<Calendar> = _calendarService.filter(filter, _pageable)

        assertEquals(calendars.size, result.totalElements.toInt())
        assertEquals(calendars.map { it.title }, result.content.map { it.title })
        verify(_calendarRepository).filter(eq(filter.title), eq(filter.emoji), eq(_pageable))
    }

    @Test
    fun `should update and return calendar`() {
        val id: UUID = _sampleCalendar.id
        val updated: Calendar = _sampleCalendar.copy(title = "Work")

        whenever(_calendarRepository.findById(id)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_calendarRepository.existsByTitle(updated.title)).thenReturn(false)
        doReturn(updated).whenever(_calendarRepository).save(any())
        val result: Calendar = _calendarService.update(id, updated.toDto())

        assertEquals(updated.title, result.title)
        verify(_calendarRepository).save(any())
    }

    @Test
    fun `should delete calendar`() {
        val id: UUID = _sampleCalendar.id

        whenever(_calendarRepository.findById(id)).thenReturn(Optional.of(_sampleCalendar))
        doNothing().whenever(_calendarRepository).delete(_sampleCalendar)
        _calendarService.delete(id)

        verify(_calendarRepository).delete(_sampleCalendar)
    }

}
