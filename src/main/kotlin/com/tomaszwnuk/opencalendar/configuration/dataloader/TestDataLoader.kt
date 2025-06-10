package com.tomaszwnuk.opencalendar.configuration.dataloader

import com.tomaszwnuk.opencalendar.domain.calendar.Calendar
import com.tomaszwnuk.opencalendar.domain.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.domain.category.Category
import com.tomaszwnuk.opencalendar.domain.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.event.Event
import com.tomaszwnuk.opencalendar.domain.event.EventRepository
import com.tomaszwnuk.opencalendar.domain.event.RecurringPattern
import com.tomaszwnuk.opencalendar.domain.note.Note
import com.tomaszwnuk.opencalendar.domain.note.NoteRepository
import com.tomaszwnuk.opencalendar.domain.task.Task
import com.tomaszwnuk.opencalendar.domain.task.TaskRepository
import com.tomaszwnuk.opencalendar.domain.task.TaskStatus
import com.tomaszwnuk.opencalendar.utility.logger.info
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Suppress("unused")
@Profile("development", "test")
@Component
class TestDataLoader(
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository,
    private val _eventRepository: EventRepository,
    private val _taskRepository: TaskRepository,
    private val _noteRepository: NoteRepository
) : CommandLineRunner {

    private var _timer: Long = System.currentTimeMillis()

    override fun run(vararg arguments: String?) {
        if (_calendarRepository.count() > 0) {
            info(source = this, message = "Test data already loaded. Skipping...")
            return
        }
        _timer = System.currentTimeMillis()

        val calendars: Map<String, Calendar> = createCalendars()
        val categories: Map<String, Category> = createCategories()

        createNotes(calendars, categories)
        createTasks(calendars, categories)
        createEvents(calendars, categories)

        info(source = this, message = "Test data loaded in ${System.currentTimeMillis() - _timer} ms")
    }

    private fun createCalendars(): Map<String, Calendar> {
        _timer = System.currentTimeMillis()

        val first: Calendar = _calendarRepository.save(
            Calendar(
                id = UUID.randomUUID(),
                name = "#1",
                emoji = "\uD83D\uDCA5"
            )
        )
        val calendars: Map<String, Calendar> = mapOf("first" to first)

        info(source = this, message = "Calendars created in ${System.currentTimeMillis() - _timer} ms")
        return calendars
    }

    private fun createCategories(): Map<String, Category> {
        _timer = System.currentTimeMillis()

        val personal: Category = _categoryRepository.save(
            Category(
                name = "Personal",
                color = "#EFEF39"
            )
        )
        val work: Category = _categoryRepository.save(
            Category(
                name = "Work",
                color = "#48DD52"
            )
        )
        val university: Category = _categoryRepository.save(
            Category(
                name = "University",
                color = "#E8475D"
            )
        )
        val categories: Map<String, Category> = mapOf(
            "personal" to personal,
            "work" to work,
            "university" to university
        )

        info(source = this, message = "Categories created in ${System.currentTimeMillis() - _timer} ms")
        return categories
    }

    private fun createEvents(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        _timer = System.currentTimeMillis()
        val now: LocalDateTime = LocalDateTime.now().withSecond(0).withNano(0)

        val work01 = Event(
            name = "Work",
            description = "Working in the office.",
            startDate = now.withHour(8).withMinute(0),
            endDate = now.withHour(12).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["work"],
            recurringPattern = RecurringPattern.DAILY
        )
        val work02 = Event(
            name = "Daily Meeting",
            description = "Team meeting.",
            startDate = now.withHour(10).withMinute(0),
            endDate = now.withHour(10).withMinute(30),
            calendar = calendars["first"]!!,
            category = categories["work"],
            recurringPattern = RecurringPattern.DAILY
        )

        val classes01 = Event(
            name = "University classes",
            description = "University classes - 01.",
            startDate = now.plusDays(1).withHour(16).withMinute(0),
            endDate = now.plusDays(1).withHour(18).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["university"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val classes02 = Event(
            name = "University classes",
            description = "University classes - 02.",
            startDate = now.plusDays(2).withHour(13).withMinute(0),
            endDate = now.plusDays(2).withHour(18).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["university"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val classes03 = Event(
            name = "University classes",
            description = "University classes - 03.",
            startDate = now.plusDays(3).withHour(12).withMinute(0),
            endDate = now.plusDays(3).withHour(15).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["university"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val classes04 = Event(
            name = "University classes",
            description = "University classes - 04.",
            startDate = now.plusDays(4).withHour(12).withMinute(0),
            endDate = now.plusDays(4).withHour(14).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )

        val workout01 = Event(
            name = "Gym",
            description = "Workout at the gym - 01.",
            startDate = now.plusDays(1).withHour(18).withMinute(0),
            endDate = now.plusDays(1).withHour(20).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val workout02 = Event(
            name = "Gym",
            description = "Workout at the gym - 03.",
            startDate = now.plusDays(2).withHour(18).withMinute(0),
            endDate = now.plusDays(2).withHour(20).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val workout03 = Event(
            name = "Gym",
            description = "Workout at the gym - 03.",
            startDate = now.plusDays(4).withHour(18).withMinute(0),
            endDate = now.plusDays(4).withHour(20).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val workout04 = Event(
            name = "Gym",
            description = "Workout at the gym - 04.",
            startDate = now.plusDays(5).withHour(18).withMinute(0),
            endDate = now.plusDays(5).withHour(20).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )

        val birthdayParty = Event(
            name = "Birthday Party",
            description = "Friend's birthday celebration.",
            startDate = now.plusDays(10).withHour(16).withMinute(0),
            endDate = now.plusDays(10).withHour(23).withMinute(59),
            calendar = calendars["first"]!!,
            recurringPattern = RecurringPattern.YEARLY
        )

        info(source = this, message = "Events created in ${System.currentTimeMillis() - _timer} ms")
        _eventRepository.saveAll(
            listOf(
                work01,
                work02,
                classes01,
                classes02,
                classes03,
                classes04,
                workout01,
                workout02,
                workout03,
                workout04,
                birthdayParty
            )
        )
    }

    private fun createTasks(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        _timer = System.currentTimeMillis()

        val walkTheDog = Task(
            name = "Walk the Dog",
            description = null,
            status = TaskStatus.TODO,
            calendar = calendars["first"]!!,
            category = categories["personal"],
        )
        val buyGroceries = Task(
            name = "Buy Groceries",
            description = null,
            status = TaskStatus.TODO,
            calendar = calendars["first"]!!,
            category = categories["personal"],
        )
        val studyForExam = Task(
            name = "Study for Exam",
            description = null,
            status = TaskStatus.IN_PROGRESS,
            calendar = calendars["first"]!!,
            category = categories["university"],
        )

        info(source = this, message = "Tasks created in ${System.currentTimeMillis() - _timer} ms")
        _taskRepository.saveAll(listOf(walkTheDog, buyGroceries, studyForExam))
    }

    private fun createNotes(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        _timer = System.currentTimeMillis()

        val shoppingList = Note(
            name = "Groceries",
            description = "- Milk,\n -Eggs,\n -Bread",
            calendar = calendars["first"]!!,
            category = categories["personal"]
        )

        info(source = this, message = "Notes created in ${System.currentTimeMillis() - _timer} ms")
        _noteRepository.save(shoppingList)
    }

}
