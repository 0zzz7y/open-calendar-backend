package com.tomaszwnuk.dailyassistant.event

import com.tomaszwnuk.dailyassistant.calendar.CalendarRepository
import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class EventService(

    private val _eventRepository: EventRepository,

    private val _calendarRepository: CalendarRepository,

    private val _categoryRepository: CategoryRepository,

) {

    fun getAll(): List<Event> = _eventRepository.findAll()

    fun getById(id: UUID): Event = _eventRepository.findById(id).orElseThrow {
        NoSuchElementException("Event with id $id could not be found.")
    }

    fun create(dto: EventDto): Event {
        val calendar = _calendarRepository.findById(dto.calendarId).orElseThrow {
            NoSuchElementException("Calendar with id ${dto.calendarId} could not be found.")
        }
        val category = dto.categoryId?.let {
            _categoryRepository.findById(it).orElseThrow {
                NoSuchElementException("Category with id $it could not be found.")
            }
        }

        val event = Event(
            name = dto.name,
            description = dto.description,
            date = dto.date,
            recurringPattern = dto.recurringPattern,
            calendar = calendar,
            category = category
        )

        return _eventRepository.save(event)
    }

    fun update(id: UUID, dto: EventDto): Event {
        val existing = getById(id)
        val calendar = _calendarRepository.findById(dto.calendarId).orElseThrow {
            NoSuchElementException("Calendar with id ${dto.calendarId} could not be found.")
        }
        val category = dto.categoryId?.let {
            _categoryRepository.findById(it).orElseThrow {
                NoSuchElementException("Category with id $it could not be found.")
            }
        }

        val updated = existing.copy(
            name = dto.name,
            description = dto.description,
            date = dto.date,
            recurringPattern = dto.recurringPattern,
            calendar = calendar,
            category = category
        )

        return _eventRepository.save(updated)
    }

    fun delete(id: UUID) {
        val event = getById(id)
        _eventRepository.delete(event)
    }

}
