package com.tomaszwnuk.dailyassistant.configuration

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.calendar.CalendarRepository
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import com.tomaszwnuk.dailyassistant.domain.RecurringPattern
import com.tomaszwnuk.dailyassistant.domain.utility.info
import com.tomaszwnuk.dailyassistant.event.Event
import com.tomaszwnuk.dailyassistant.event.EventRepository
import com.tomaszwnuk.dailyassistant.note.Note
import com.tomaszwnuk.dailyassistant.note.NoteRepository
import com.tomaszwnuk.dailyassistant.task.Task
import com.tomaszwnuk.dailyassistant.task.TaskRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Suppress("unused")
@Profile("dev", "test")
@Component
class TestDataLoader(
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository,
    private val _eventRepository: EventRepository,
    private val _taskRepository: TaskRepository,
    private val _noteRepository: NoteRepository
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val startTime: Long = System.currentTimeMillis()
        val calendars: Map<String, Calendar> = createCalendars()
        val categories: Map<String, Category> = createCategories()

        createNotes(calendars, categories)
        createTasks(calendars, categories)
        createEvents(calendars, categories)

        info(this, "Test data loaded in ${System.currentTimeMillis() - startTime} ms")
    }

    private fun createCalendars(): Map<String, Calendar> {
        val startTime: Long = System.currentTimeMillis()
        val personal: Calendar =
            _calendarRepository.save(Calendar(id = UUID.randomUUID(), name = "Personal", emoji = "🏠"))
        val work: Calendar = _calendarRepository.save(Calendar(id = UUID.randomUUID(), name = "Work", emoji = "💼"))
        val calendars: Map<String, Calendar> = mapOf(
            "personal" to personal,
            "work" to work
        )

        info(this, "Calendars created in ${System.currentTimeMillis() - startTime} ms")
        return calendars
    }

    private fun createCategories(): Map<String, Category> {
        val startTime: Long = System.currentTimeMillis()
        val personal: Category =
            _categoryRepository.save(Category(name = "Personal", color = "#EFEF39"))
        val work: Category =
            _categoryRepository.save(Category(name = "Work", color = "#48DD52"))
        val university: Category =
            _categoryRepository.save(Category(name = "University", color = "#E8475D"))
        val categories: Map<String, Category> = mapOf(
            "personal" to personal,
            "work" to work,
            "university" to university
        )

        info(this, "Categories created in ${System.currentTimeMillis() - startTime} ms")
        return categories
    }

    private fun createEvents(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        val startTime: Long = System.currentTimeMillis()
        val now: LocalDateTime = LocalDateTime.now()
        val dailyStandup = Event(
            name = "Daily Standup",
            description = "Team sync-up.",
            startDate = now.withHour(9),
            endDate = now.withHour(9).plusMinutes(30),
            calendar = calendars["work"]!!,
            category = categories["work"],
            recurringPattern = RecurringPattern.DAILY
        )
        val birthdayParty = Event(
            name = "Birthday Party",
            description = "Friend's birthday celebration.",
            startDate = now.plusDays(10).withHour(18),
            endDate = now.plusDays(10).withHour(23),
            calendar = calendars["personal"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.YEARLY
        )
        val studyForExam = Event(
            name = "Study for Exam",
            description = "Study for final term exam.",
            startDate = now.plusDays(10).withHour(18),
            endDate = now.plusDays(10).withHour(23),
            calendar = calendars["personal"]!!,
            category = categories["university"],
            recurringPattern = RecurringPattern.YEARLY
        )

        info(this, "Events created in ${System.currentTimeMillis() - startTime} ms")
        _eventRepository.saveAll(listOf(dailyStandup, birthdayParty, studyForExam))
    }

    private fun createTasks(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        val startTime: Long = System.currentTimeMillis()
        val now: LocalDateTime = LocalDateTime.now()
        val doShopping = Task(
            name = "Do Shopping",
            description = "There is nothing in the fridge.",
            startDate = now.plusDays(1).withHour(17),
            endDate = now.plusDays(1).withHour(18),
            calendar = calendars["personal"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val walkTheDog = Task(
            name = "Walk the Dog",
            description = "Walk the dog.",
            startDate = now.plusDays(2).withHour(17),
            endDate = now.plusDays(2).withHour(18),
            calendar = calendars["personal"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.DAILY
        )
        val studyForExam = Task(
            name = "Study for Exam",
            description = "Study for final term exam.",
            startDate = now.plusDays(5).withHour(17),
            endDate = now.plusDays(5).withHour(18),
            calendar = calendars["personal"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.NONE
        )

        info(this, "Tasks created in ${System.currentTimeMillis() - startTime} ms")
        _taskRepository.saveAll(listOf(doShopping, walkTheDog, studyForExam))
    }

    private fun createNotes(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        val startTime: Long = System.currentTimeMillis()
        val shoppingList = Note(
            name = "Shopping List",
            description = "Milk, Eggs, Bread",
            calendar = calendars["personal"]!!,
            category = categories["personal"]
        )

        info(this, "Notes created in ${System.currentTimeMillis() - startTime} ms")
        _noteRepository.save(shoppingList)
    }

}
