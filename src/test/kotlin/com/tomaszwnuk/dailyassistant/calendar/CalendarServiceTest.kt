package com.tomaszwnuk.dailyassistant.calendar

import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_SIZE
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
            name = "Personal",
            emoji = "🏠"
        )
        _sampleDto = _sampleCalendar.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should create and return calendar`() {
        whenever(_calendarRepository.existsByName(_sampleCalendar.name)).thenReturn(false)
        doReturn(_sampleCalendar).whenever(_calendarRepository).save(any())
        val result: Calendar = _calendarService.create(_sampleDto)

        assertEquals(_sampleCalendar.name, result.name)
        verify(_calendarRepository).save(any())
    }

    @Test
    fun `should return paginated list of calendars`() {
        val calendars = listOf(_sampleCalendar, _sampleCalendar, _sampleCalendar)

        whenever(_calendarRepository.findAll(_pageable)).thenReturn(PageImpl(calendars))
        val result: Page<Calendar> = _calendarService.getAll(_pageable)

        assertEquals(calendars.size, result.totalElements.toInt())
        verify(_calendarRepository).findAll(_pageable)
    }

    @Test
    fun `should return calendar by id`() {
        val id = _sampleCalendar.id

        whenever(_calendarRepository.findById(id)).thenReturn(Optional.of(_sampleCalendar))
        val result: Calendar = _calendarService.getById(id)

        assertEquals(_sampleCalendar.id, result.id)
        verify(_calendarRepository).findById(id)
    }

    @Test
    fun `should return filtered calendars`() {
        val filter = CalendarFilterDto(name = "Personal")
        val calendars: List<Calendar> = listOf(_sampleCalendar, _sampleCalendar, _sampleCalendar)

        whenever(
            _calendarRepository.filter(
                eq(filter.name),
                eq(filter.emoji),
                eq(_pageable)
            )
        ).thenReturn(PageImpl(calendars))
        val result: Page<Calendar> = _calendarService.filter(filter, _pageable)

        assertEquals(calendars.size, result.totalElements.toInt())
        verify(_calendarRepository).filter(
            eq(filter.name),
            eq(filter.emoji),
            eq(_pageable)
        )
    }

    @Test
    fun `should update and return calendar`() {
        val id: UUID = _sampleCalendar.id
        val updated: Calendar = _sampleCalendar.copy(name = "Work")

        whenever(_calendarRepository.findById(id)).thenReturn(Optional.of(_sampleCalendar))
        whenever(_calendarRepository.existsByName(updated.name)).thenReturn(false)
        doReturn(updated).whenever(_calendarRepository).save(any())
        val result: Calendar = _calendarService.update(id, updated.toDto())

        assertEquals(updated.name, result.name)
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