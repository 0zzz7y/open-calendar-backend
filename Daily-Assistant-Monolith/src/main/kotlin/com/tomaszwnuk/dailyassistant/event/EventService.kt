package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.calendar.CalendarRepository
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import com.tomaszwnuk.dailyassistant.domain.info
import com.tomaszwnuk.dailyassistant.domain.validation.findOrThrow
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventService(
    private val _eventRepository: EventRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository,
) {

    fun getAll(): List<Event> {
        info(this, "Fetching all events")
        return _eventRepository.findAll()
    }

    fun getById(id: UUID): Event {
        info(this, "Fetching event with id $id")
        val event: Event = _eventRepository.findOrThrow(id)

        info(this, "Found $event")
        return event
    }

    fun create(dto: EventDto): Event {
        info(this, "Creating $dto")
        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }

        val event = Event(
            name = dto.name,
            description = dto.description,
            date = dto.date,
            recurringPattern = dto.recurringPattern,
            calendar = calendar,
            category = category
        )

        info(this, "Created $event")
        return _eventRepository.save(event)
    }

    fun update(id: UUID, dto: EventDto): Event {
        info(this, "Updating $dto")
        val existing: Event = getById(id)
        val calendar: Calendar = _calendarRepository.findOrThrow(id = dto.calendarId)
        val category: Category? = dto.categoryId?.let { _categoryRepository.findOrThrow(id = it) }

        val updated: Event = existing.copy(
            name = dto.name,
            description = dto.description,
            date = dto.date,
            recurringPattern = dto.recurringPattern,
            calendar = calendar,
            category = category
        )

        info(this, "Updated $updated")
        return _eventRepository.save(updated)
    }

    fun delete(id: UUID) {
        info(this, "Deleting event with id $id.")
        val event: Event = getById(id)

        info(this, "Deleting event $event")
        _eventRepository.delete(event)
    }

}
