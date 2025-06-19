package com.ozzz7y.opencalendar.domain.event

import com.ozzz7y.opencalendar.domain.calendar.Calendar
import com.ozzz7y.opencalendar.domain.calendar.CalendarRepository
import com.ozzz7y.opencalendar.domain.category.Category
import com.ozzz7y.opencalendar.domain.category.CategoryRepository
import com.ozzz7y.opencalendar.domain.user.UserService
import com.ozzz7y.opencalendar.utility.logger.info
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.stereotype.Service
import java.util.*

/**
 * The service for event operations.
 */
@Service
class EventService(

    /**
     * The repository for managing event data.
     */
    private val _eventRepository: EventRepository,

    /**
     * The repository for managing calendar data.
     */
    private val _calendarRepository: CalendarRepository,

    /**
     * The repository for managing category data.
     */
    private val _categoryRepository: CategoryRepository,

    /**
     * The service for user operations.
     */
    private val _userService: UserService

) {

    /**
     * The timer for measuring the duration of operations.
     */
    private var _timer: Long = 0

    /**
     * Creates a new event.
     *
     * @param dto The data transfer object containing event details
     *
     * @return The created event as a data transfer object
     *
     * @throws IllegalArgumentException If the calendar does not exist for the user
     */
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
        val calendar: Optional<Calendar> = _calendarRepository.findByIdAndUserId(id = dto.calendarId, userId = userId)
        if (calendar.isEmpty) {
            throw IllegalArgumentException("Calendar with id ${dto.calendarId} not found for user $userId")
        }

        val category: Optional<Category>? = dto.categoryId?.let {
            _categoryRepository.findByIdAndUserId(id = it, userId = userId)
        }

        val event = Event(
            name = dto.name,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            recurringPattern = dto.recurringPattern,
            calendar = calendar.get(),
            category = category?.get()
        )

        val created: Event = _eventRepository.save(event)
        info(source = this, message = "Created $created in ${System.currentTimeMillis() - _timer} ms")
        return created.toDto()
    }

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id The unique identifier of the event
     *
     * @return The event as a data transfer object
     *
     * @throws NoSuchElementException If the event does not exist for the user
     */
    @Cacheable(cacheNames = ["eventById"], key = "#id", condition = "#id != null")
    fun getById(id: UUID): EventDto {
        info(source = this, message = "Fetching event with id $id")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val event: Optional<Event> = _eventRepository.findByIdAndCalendarUserId(id = id, userId = userId)
        if (event.isEmpty) {
            throw NoSuchElementException("Event with id $id not found for user $userId")
        }

        info(source = this, message = "Found $event in ${System.currentTimeMillis() - _timer} ms")
        return event.get().toDto()
    }

    /**
     * Retrieves all events for the current user.
     *
     * @return A list of all events as data transfer objects
     */
    @Cacheable(cacheNames = ["allEvents"], condition = "#result != null")
    fun getAll(): List<EventDto> {
        info(source = this, message = "Fetching all events")
        _timer = System.currentTimeMillis()

        val userId: UUID = _userService.getCurrentUserId()
        val events: List<Event> = _eventRepository.findAllByCalendarUserId(userId = userId)

        info(source = this, message = "Found $events in ${System.currentTimeMillis() - _timer} ms")
        return events.map { it.toDto() }
    }

    /**
     * Retrieves all events associated with a specific calendar.
     *
     * @param calendarId The unique identifier of the calendar
     *
     * @return A list of events associated with the specified calendar as data transfer objects
     */
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

    /**
     * Retrieves all events associated with a specific category.
     *
     * @param categoryId The unique identifier of the category
     *
     * @return A list of events associated with the specified category as data transfer objects
     */
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

    /**
     * Filters events based on the provided criteria.
     *
     * @param filter The filter criteria as a data transfer object
     *
     * @return A list of events that match the filter criteria
     */
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

    /**
     * Updates an existing event.
     *
     * @param id The unique identifier of the event to update
     * @param dto The data transfer object containing the updated details of the event
     *
     * @return The updated event as a data transfer object
     *
     * @throws NoSuchElementException If the event does not exist for the user
     * @throws IllegalArgumentException If the calendar does not exist for the user
     */
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
        val existing: Optional<Event> = _eventRepository.findByIdAndCalendarUserId(id = id, userId = userId)
        if (existing.isEmpty) {
            throw NoSuchElementException("Event with id $id not found for user $userId")
        }

        val calendar: Optional<Calendar> = _calendarRepository.findByIdAndUserId(id = dto.calendarId, userId = userId)
        if (calendar.isEmpty) {
            throw IllegalArgumentException("Calendar with id ${dto.calendarId} not found for user $userId")
        }

        val category: Optional<Category>? = dto.categoryId?.let {
            _categoryRepository.findByIdAndUserId(id = it, userId = userId)
        }

        val updated = existing.get().copy(
            name = dto.name,
            description = dto.description,
            startDate = dto.startDate,
            endDate = dto.endDate,
            recurringPattern = dto.recurringPattern,
            calendar = calendar.get(),
            category = category?.get()
        )

        val saved: Event = _eventRepository.save(updated)
        info(source = this, message = "Updated $saved in ${System.currentTimeMillis() - _timer} ms")
        return saved.toDto()
    }

    /**
     * Deletes an event by its unique identifier.
     *
     * @param id The unique identifier of the event to delete
     *
     * @throws NoSuchElementException If the event does not exist for the user
     */
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
        val existing: Optional<Event> = _eventRepository.findByIdAndCalendarUserId(id = id, userId = userId)
        if (existing.isEmpty) {
            throw NoSuchElementException("Event with id $id not found for user $userId")
        }

        _eventRepository.delete(existing.get())
        info(source = this, message = "Deleted event $existing in \${System.currentTimeMillis() - _timer} ms")
    }

    /**
     * Deletes all events associated with a specific calendar.
     *
     * @param calendarId The unique identifier of the calendar
     */
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

    /**
     * Removes the category from all events associated with a specific category.
     *
     * @param categoryId The unique identifier of the category
     */
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
            message = "Updated category to null for all events in ${System.currentTimeMillis() - _timer} ms"
        )
    }

}
