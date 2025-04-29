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
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CalendarServiceTest {

    @Mock
    private lateinit var calendarRepository: CalendarRepository

    @InjectMocks
    private lateinit var calendarService: CalendarService

    private lateinit var sampleCalendar: Calendar

    private lateinit var sampleCalendarDto: CalendarDto

    private lateinit var pageable: Pageable

    @BeforeEach
    fun setup() {
        sampleCalendar = Calendar(
            id = UUID.randomUUID(),
            title = "Personal",
            emoji = "\uD83C\uDFE0"
        )
        sampleCalendarDto = sampleCalendar.toDto()
        pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should create and return calendar`() {
        val id: UUID = sampleCalendar.id
        val title: String = sampleCalendar.title
        val emoji: String = sampleCalendar.emoji

        whenever(calendarRepository.existsByTitle(title)).thenReturn(false)
        doReturn(sampleCalendar).whenever(calendarRepository).save(any())

        val result: Calendar = calendarService.create(sampleCalendarDto)

        assertNotNull(result)
        assertEquals(id, result.id)
        assertEquals(title, result.title)
        assertEquals(emoji, result.emoji)

        verify(calendarRepository).save(any())
    }

    @Test
    fun `should return paginated list of all calendars`() {
        val calendars: List<Calendar> = listOf(
            sampleCalendar,
            sampleCalendar.copy(id = UUID.randomUUID()),
            sampleCalendar.copy(id = UUID.randomUUID())
        )
        whenever(calendarRepository.findAll(pageable)).thenReturn(PageImpl(calendars))

        val result: Page<Calendar> = calendarService.getAll(pageable)

        assertEquals(calendars.size, result.totalElements.toInt())
        assertEquals(calendars.map { it.id }, result.content.map { it.id })
        assertEquals(calendars.map { it.title }, result.content.map { it.title })

        verify(calendarRepository).findAll(pageable)
    }

    @Test
    fun `should return calendar by id`() {
        val id: UUID = sampleCalendar.id
        whenever(calendarRepository.findById(id)).thenReturn(Optional.of(sampleCalendar))

        val result: Calendar = calendarService.getById(id)

        assertNotNull(result)
        assertEquals(id, result.id)
        assertEquals(sampleCalendar.title, result.title)
        assertEquals(sampleCalendar.emoji, result.emoji)

        verify(calendarRepository).findById(id)
    }

    @Test
    fun `should return paginated list of filtered calendars`() {
        val filter = CalendarFilterDto(title = "Personal")
        val calendars: List<Calendar> = listOf(
            sampleCalendar,
            sampleCalendar.copy(id = UUID.randomUUID()),
            sampleCalendar.copy(id = UUID.randomUUID())
        )
        whenever(calendarRepository.filter(eq(filter.title), eq(filter.emoji), eq(pageable)))
            .thenReturn(PageImpl(calendars))

        val result: Page<Calendar> = calendarService.filter(filter, pageable)

        assertEquals(calendars.size, result.totalElements.toInt())
        assertEquals(calendars.map { it.title }, result.content.map { it.title })

        verify(calendarRepository).filter(eq(filter.title), eq(filter.emoji), eq(pageable))
    }

    @Test
    fun `should update and return updated calendar`() {
        val id: UUID = sampleCalendar.id
        val updatedTitle = "Work"
        val updatedCalendar: Calendar = sampleCalendar.copy(title = updatedTitle)

        whenever(calendarRepository.findById(id)).thenReturn(Optional.of(sampleCalendar))
        whenever(calendarRepository.existsByTitle(updatedTitle)).thenReturn(false)
        doReturn(updatedCalendar).whenever(calendarRepository).save(any())

        val result: Calendar = calendarService.update(id, updatedCalendar.toDto())

        assertNotNull(result)
        assertEquals(updatedCalendar.id, result.id)
        assertEquals(updatedTitle, result.title)
        assertEquals(updatedCalendar.emoji, result.emoji)

        verify(calendarRepository).save(any())
    }

    @Test
    fun `should delete calendar by id`() {
        val id: UUID = sampleCalendar.id
        whenever(calendarRepository.findById(id)).thenReturn(Optional.of(sampleCalendar))
        doNothing().whenever(calendarRepository).delete(sampleCalendar)

        calendarService.delete(id)

        verify(calendarRepository).delete(sampleCalendar)
    }

}
