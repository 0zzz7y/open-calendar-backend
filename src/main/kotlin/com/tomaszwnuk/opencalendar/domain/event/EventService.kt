package com.tomaszwnuk.opencalendar.domain.event

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.user.UserService
import com.tomaszwnuk.opencalendar.utility.logger.info
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventService(
    private val _eventRepository: EventRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository,
    private val _userService: UserService
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
        info(source = this, message = "Creating $dto")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val calendar: Calendar = _calendarRepository.findByIdAndUserId(id = dto.calendarId, userId = userId).orElseThrow {
            IllegalArgumentException("Calendar with id ${dto.calendarId} not found for user $userId")
        }
        val category: Category? = dto.categoryId?.let {
            _categoryRepository.findByIdAndUserId(id = it, userId = userId).orElseThrow{
                IllegalArgumentException("Category with id $it not found for user $userId")
            }
        }
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

        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    @Cacheable(cacheNames = ["eventById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): EventDto {
        info(source = this, message = "Fetching event with id $id")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val event: Event = _eventRepository.findByIdAndCalendarUserId(id = id, userId = userId).orElseThrow {
            IllegalArgumentException("Event with id $id not found for user $userId")
        }

        info(source = this, message = "Found $event in ${System.currentTimeMillis() - _timer} ms")
        return event.toDto()
    }

    @Cacheable(cacheNames = ["allEvents"], condition = "#result != null")
    fun getAll(): List<EventDto> {
        info(source = this, message = "Fetching all events")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val events: List<Event> = _eventRepository.findAllByCalendarUserId(userId = userId)

        info(source = this, message = "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["calendarEvents"], key = "#calendarId", condition = "#calendarId != null")
    fun getAllByCalendarId(calendarId: UUID): List<EventDto> {
        info(source = this, message = "Fetching all events for calendar with id $calendarId")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val events: List<Event> = _eventRepository.findAllByCalendarIdAndCalendarUserId(
            calendarId = calendarId,
            userId = userId
        )

        info(source = this, message = "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events.map { it.toDto() }
    }

    @Cacheable(cacheNames = ["categoryEvents"], key = "#categoryId", condition = "#categoryId != null")
    fun getAllByCategoryId(categoryId: UUID): List<EventDto> {
        info(source = this, message = "Fetching all events for category with id $categoryId")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val events: List<Event> = _eventRepository.findAllByCategoryIdAndCalendarUserId(
            categoryId = categoryId,
            userId = userId
        )

        info(source = this, message = "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events.map { it.toDto() }
    }

    fun filter(filter: EventFilterDto): List<EventDto> {
        info(source = this, message = "Filtering events with $filter")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val filtered: List<Event> = _eventRepository.filter(
            userId = userId,
            name = filter.name,
            description = filter.description,
            dateFrom = filter.dateFrom,
            dateTo = filter.dateTo,
            recurringPattern = filter.recurringPattern,
            calendarId = filter.calendarId,
            categoryId = filter.categoryId
        )

        info(source = this, message = "Found $filtered in ${System.currentTimeMillis() - _timer} ms")
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
        info(source = this, message = "Updating $dto")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Event = _eventRepository.findByIdAndCalendarUserId(id = id, userId = userId).orElseThrow {
            IllegalArgumentException("Event with id $id not found for user $userId")
        }
        val calendar: Calendar = _calendarRepository.findByIdAndUserId(id = dto.calendarId, userId = userId).orElseThrow {
            IllegalArgumentException("Calendar with id ${dto.calendarId} not found for user $userId")
        }
        val category: Category? = dto.categoryId?.let { _categoryRepository.findByIdAndUserId(id = it, userId = userId).orElseThrow {
            IllegalArgumentException("Category with id $it not found for user $userId")
        } }
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

        info(source = this, message = "Updated $saved in ${System.currentTimeMillis() - _timer} ms")
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
        info(source = this, message = "Deleting event with id $id.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val existing: Event = _eventRepository.findByIdAndCalendarUserId(id = id, userId = userId).orElseThrow {
            IllegalArgumentException("Event with id $id not found for user $userId")
        }

        _eventRepository.delete(existing)
        info(source = this, message = "Deleted event $existing in \${System.currentTimeMillis() - _timer} ms")
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["eventById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun deleteAllByCalendarId(calendarId: UUID) {
        info(source = this, message = "Deleting all events for calendar with id $calendarId.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val events: List<Event> = _eventRepository.findAllByCalendarIdAndCalendarUserId(
            calendarId = calendarId,
            userId = userId
        )
        _eventRepository.deleteAll(events)

        info(
            source = this,
            message = "Deleted all events for calendar with id $calendarId in ${System.currentTimeMillis() - _timer} ms"
        )
    }

    @Caching(
        evict = [
            CacheEvict(cacheNames = ["eventById"], key = "#id", condition = "#id != null"),
            CacheEvict(cacheNames = ["allEvents"], allEntries = true),
            CacheEvict(cacheNames = ["calendarEvents"], allEntries = true),
            CacheEvict(cacheNames = ["categoryEvents"], allEntries = true)
        ]
    )
    fun removeCategoryByCategoryId(categoryId: UUID) {
        info(source = this, message = "Deleting all events for category with id $categoryId.")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val events: List<Event> = _eventRepository.findAllByCategoryIdAndCalendarUserId(
            categoryId = categoryId,
            userId = userId
        )
        events.forEach { event ->
            val withoutCategory = event.copy(category = null)
            _eventRepository.save(withoutCategory)
        }

        info(
            source = this,
            message = "Deleted all events for category with id $categoryId in ${System.currentTimeMillis() - _timer} ms"
        )
    }

}
