package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.calendar.CalendarRepository
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import com.tomaszwnuk.dailyassistant.domain.utility.info
import com.tomaszwnuk.dailyassistant.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventService(
    private val _eventRepository: EventRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository
) {

    private var _timer: Long = 0

    @Caching(evict = [
        CacheEvict(cacheNames = ["calendarEvents"], key = "#dto.calendarId")
    ])
    fun create(dto: EventDto): Event {
        info(this, "Creating $dto")
        _timer = System.currentTimeMillis()
        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val event = Event(
            name = dto.name,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            recurringPattern = dto.recurringPattern,
            calendar = calendar,
            category = category
        )

        val created: Event = _eventRepository.save(event)
        info(this, "Created $created in ${System.currentTimeMillis() - _timer} ms")

        return created
    }

    @Cacheable(cacheNames = ["eventById"], key = "#id")
    fun getById(id: UUID): Event {
        info(this, "Fetching event with id $id")
        _timer = System.currentTimeMillis()
        val event: Event = _eventRepository.findOrThrow(id)

        info(this, "Found $event in ${System.currentTimeMillis() - _timer} ms")
        return event
    }

    fun getAll(pageable: Pageable): Page<Event> {
        info(this, "Fetching all events")
        _timer = System.currentTimeMillis()
        val events: Page<Event> = _eventRepository.findAll(pageable)

        info(this, "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events
    }

    @Cacheable(cacheNames = ["calendarEvents"], key = "#calendarId")
    fun getAllByCalendarId(calendarId: UUID, pageable: Pageable): Page<Event> {
        info(this, "Fetching all events for calendar with id $calendarId")
        _timer = System.currentTimeMillis()
        val events: Page<Event> = _eventRepository.findAllByCalendarId(calendarId, pageable)

        info(this, "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events
    }

    fun filter(filter: EventFilterDto, pageable: Pageable): Page<Event> {
        info(this, "Filtering events with $filter")
        _timer = System.currentTimeMillis()
        val filtered: Page<Event> = _eventRepository.filter(
            name = filter.name,
            description = filter.description,
            dateFrom = filter.dateFrom,
            dateTo = filter.dateTo,
            recurringPattern = filter.recurringPattern,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId,
            pageable = pageable
        )

        info(this, "Found $filtered in ${System.currentTimeMillis() - _timer} ms")
        return filtered
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["calendarEvents"], key = "#existing.calendar.id"),
        CacheEvict(cacheNames = ["eventById"], key = "#id")
    ])
    fun update(id: UUID, dto: EventDto): Event {
        info(this, "Updating $dto")
        _timer = System.currentTimeMillis()

        val existing: Event = getById(id)
        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }

        val updated = existing.copy(
            name = dto.name,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            recurringPattern = dto.recurringPattern,
            calendar = calendar,
            category = category
        )

        val saved: Event = _eventRepository.save(updated)
        info(this, "Updated $saved in ${System.currentTimeMillis() - _timer} ms")

        return saved
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["calendarEvents"], key = "#existing.calendarId"),
        CacheEvict(cacheNames = ["eventById"], key = "#id")
    ])
    fun delete(id: UUID) {
        info(this, "Deleting event with id $id.")
        _timer = System.currentTimeMillis()
        val existing: Event = getById(id)

        _eventRepository.delete(existing)
        info(this, "Deleted event $existing in ${System.currentTimeMillis() - _timer} ms")
    }

    @Caching(evict = [
        CacheEvict(cacheNames = ["calendarEvents"], key = "#calendarId"),
        CacheEvict(cacheNames = ["eventById"], allEntries = true)
    ])
    fun deleteAllByCalendarId(calendarId: UUID) {
        info(this, "Deleting all events for calendar with id $calendarId.")
        _timer = System.currentTimeMillis()
        val events: Page<Event> = _eventRepository.findAllByCalendarId(
            calendarId = calendarId,
            pageable = Pageable.unpaged()
        )

        _eventRepository.deleteAll(events)
        info(this, "Deleted all events for calendar with id $calendarId in ${System.currentTimeMillis() - _timer} ms")
    }

}
