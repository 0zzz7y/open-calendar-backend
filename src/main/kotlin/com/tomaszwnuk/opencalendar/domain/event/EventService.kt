package com.tomaszwnuk.opencalendar.domain.event

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.utility.logger.info
import com.tomaszwnuk.opencalendar.utility.validation.findOrThrow
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventService(
    private val _eventRepository: EventRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository
) {

    private var _timer: Long = 0

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun create(dto: EventDto): EventDto {
        info(this, "Creating $dto")
        _timer = System.currentTimeMillis()

        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val event = Event(
            title = dto.title,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            recurringPattern = dto.recurringPattern,
            calendar = calendar,
            category = category
        )

        val created: Event = _eventRepository.save(event)

        info(this, "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    @Cacheable(cacheNames = ["eventById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): EventDto {
        info(this, "Fetching event with id $id")
        _timer = System.currentTimeMillis()

        val event: Event = _eventRepository.findOrThrow(id)

        info(this, "Found $event in ${System.currentTimeMillis() - _timer} ms")
        return event.toDto()
    }

    @Cacheable(cacheNames = ["allEvents"], condition = "#result != null")
    fun getAll(): List<EventDto> {
        info(this, "Fetching all events")
        _timer = System.currentTimeMillis()

        val events: List<Event> = _eventRepository.findAll()

        info(this, "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["calendarEvents"], key = "#calendarId", condition = "#calendarId != null")
    fun getAllByCalendarId(calendarId: UUID): List<EventDto> {
        info(this, "Fetching all events for calendar with id $calendarId")
        _timer = System.currentTimeMillis()

        val events: List<Event> = _eventRepository.findAllByCalendarId(calendarId)

        info(this, "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["categoryEvents"], key = "#categoryId", condition = "#categoryId != null")
    fun getAllByCategoryId(categoryId: UUID): List<EventDto> {
        info(this, "Fetching all events for category with id $categoryId")
        _timer = System.currentTimeMillis()

        val events: List<Event> = _eventRepository.findAllByCategoryId(categoryId)

        info(this, "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events.map { it.toDto() }
    }

    fun filter(filter: EventFilterDto): List<EventDto> {
        info(this, "Filtering events with $filter")
        _timer = System.currentTimeMillis()

        val filtered: List<Event> = _eventRepository.filter(
            title = filter.title,
            description = filter.description,
            dateFrom = filter.dateFrom,
            dateTo = filter.dateTo,
            recurringPattern = filter.recurringPattern,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId
        )

        info(this, "Found $filtered in ${System.currentTimeMillis() - _timer} ms")
        return filtered.map { it.toDto() }
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["eventById"], key = "#id"),
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun update(id: UUID, dto: EventDto): EventDto {
        info(this, "Updating $dto")
        _timer = System.currentTimeMillis()

        val existing: Event = _eventRepository.findOrThrow(id = id)
        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }
        val updated = existing.copy(
            title = dto.title,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            recurringPattern = dto.recurringPattern,
            calendar = calendar,
            category = category
        )

        val saved: Event = _eventRepository.save(updated)

        info(this, "Updated $saved in ${System.currentTimeMillis() - _timer} ms")
        return saved.toDto()
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["eventById"], key = "#id"),
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun delete(id: UUID) {
        info(this, "Deleting event with id $id.")
        _timer = System.currentTimeMillis()

        val existing: Event = _eventRepository.findOrThrow(id)

        _eventRepository.delete(existing)
        info(this, "Deleted event $existing in \${System.currentTimeMillis() - _timer} ms")
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["eventById"], key = "#id"),
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun deleteAllByCalendarId(calendarId: UUID) {
        info(this, "Deleting all events for calendar with id $calendarId.")
        _timer = System.currentTimeMillis()

        val events: List<Event> = _eventRepository.findAllByCalendarId(calendarId)

        _eventRepository.deleteAll(events)
        info(this, "Deleted all events for calendar with id $calendarId in ${System.currentTimeMillis() - _timer} ms")
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["eventById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun deleteAllByCategoryId(categoryId: UUID) {
        info(this, "Deleting all events for category with id $categoryId.")
        _timer = System.currentTimeMillis()

        val events: List<Event> = _eventRepository.findAllByCategoryId(categoryId = categoryId)
        events.forEach { event ->
            val withoutCategory = event.copy(category = null)
            _eventRepository.save(withoutCategory)
        }

        _eventRepository.deleteAll(events)
        info(this, "Deleted all events for category with id $categoryId in ${System.currentTimeMillis() - _timer} ms")
    }

}
