package com.tomaszwnuk.opencalendar.configuration

import com.tomaszwnuk.opencalendar.calendar.Calendar
import com.tomaszwnuk.opencalendar.calendar.CalendarRepository
import com.tomaszwnuk.opencalendar.category.Category
import com.tomaszwnuk.opencalendar.category.CategoryRepository
import com.tomaszwnuk.opencalendar.domain.RecurringPattern
import com.tomaszwnuk.opencalendar.event.Event
import com.tomaszwnuk.opencalendar.event.EventRepository
import com.tomaszwnuk.opencalendar.note.Note
import com.tomaszwnuk.opencalendar.note.NoteRepository
import com.tomaszwnuk.opencalendar.task.Task
import com.tomaszwnuk.opencalendar.task.TaskRepository
import com.tomaszwnuk.opencalendar.task.TaskStatus
import com.tomaszwnuk.opencalendar.utility.info
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Suppress("unused")
@Profile("production", "development", "test")
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
            info(this, "Test data already loaded. Skipping...")
            return
        }
        _timer = System.currentTimeMillis()

        val calendars: Map<String, Calendar> = createCalendars()
        val categories: Map<String, Category> = createCategories()

        createNotes(calendars, categories)
        createTasks(calendars, categories)
        createEvents(calendars, categories)

        info(this, "Test data loaded in ${System.currentTimeMillis() - _timer} ms")
    }

    private fun createCalendars(): Map<String, Calendar> {
        _timer = System.currentTimeMillis()
        val first: Calendar =
            _calendarRepository.save(Calendar(id = UUID.randomUUID(), title = "#1", emoji = "\uD83D\uDCA5"))
        val calendars: Map<String, Calendar> = mapOf(
            "first" to first,
        )

        info(this, "Calendars created in ${System.currentTimeMillis() - _timer} ms")
        return calendars
    }

    private fun createCategories(): Map<String, Category> {
        _timer = System.currentTimeMillis()
        val personal: Category =
            _categoryRepository.save(Category(title = "Personal", color = "#EFEF39"))
        val work: Category =
            _categoryRepository.save(Category(title = "Work", color = "#48DD52"))
        val university: Category =
            _categoryRepository.save(Category(title = "University", color = "#E8475D"))
        val categories: Map<String, Category> = mapOf(
            "personal" to personal,
            "work" to work,
            "university" to university
        )

        info(this, "Categories created in ${System.currentTimeMillis() - _timer} ms")
        return categories
    }

    private fun createEvents(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        _timer = System.currentTimeMillis()
        val now: LocalDateTime = LocalDateTime.now().withSecond(0).withNano(0)

        val workingAtTheOffice = Event(
            title = "Working at the Office",
            description = "Drinking coffee and checking emails.",
            startDate = now.withHour(8).withMinute(0),
            endDate = now.withHour(12).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["work"],
            recurringPattern = RecurringPattern.DAILY
        )
        val dailyMeeting = Event(
            title = "Daily Meeting",
            description = "Daily organizational team meeting.",
            startDate = now.withHour(10).withMinute(0),
            endDate = now.withHour(10).withMinute(30),
            calendar = calendars["first"]!!,
            category = categories["work"],
            recurringPattern = RecurringPattern.DAILY
        )

        val universityClasses01 = Event(
            title = "University classes",
            description = "University classes - 01.",
            startDate = now.plusDays(1).withHour(16).withMinute(0),
            endDate = now.plusDays(1).withHour(18).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["university"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val universityClasses02 = Event(
            title = "University classes",
            description = "University classes - 02.",
            startDate = now.plusDays(2).withHour(13).withMinute(0),
            endDate = now.plusDays(2).withHour(18).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["university"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val universityClasses03 = Event(
            title = "University classes",
            description = "University classes - 03.",
            startDate = now.plusDays(3).withHour(12).withMinute(0),
            endDate = now.plusDays(3).withHour(15).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["university"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val universityClasses04 = Event(
            title = "University classes",
            description = "University classes - 04.",
            startDate = now.plusDays(4).withHour(12).withMinute(0),
            endDate = now.plusDays(4).withHour(14).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )

        val gymBicepsAndBackWorkout = Event(
            title = "Gym",
            description = "Biceps and back workout at the gym.",
            startDate = now.plusDays(1).withHour(18).withMinute(0),
            endDate = now.plusDays(1).withHour(20).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val gymTricepsAndChestWorkout = Event(
            title = "Gym",
            description = "Triceps and chest workout at the gym.",
            startDate = now.plusDays(2).withHour(18).withMinute(0),
            endDate = now.plusDays(2).withHour(20).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val gymCoreWorkout = Event(
            title = "Gym",
            description = "Core workout at the gym.",
            startDate = now.plusDays(4).withHour(18).withMinute(0),
            endDate = now.plusDays(4).withHour(20).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val gymLegWorkout = Event(
            title = "Gym",
            description = "Leg workout at the gym.",
            startDate = now.plusDays(5).withHour(18).withMinute(0),
            endDate = now.plusDays(5).withHour(20).withMinute(0),
            calendar = calendars["first"]!!,
            category = categories["personal"],
            recurringPattern = RecurringPattern.WEEKLY
        )

        val birthdayParty = Event(
            title = "Birthday Party",
            description = "Friend's birthday celebration.",
            startDate = now.plusDays(10).withHour(16).withMinute(0),
            endDate = now.plusDays(10).withHour(23).withMinute(59),
            calendar = calendars["first"]!!,
            recurringPattern = RecurringPattern.YEARLY
        )

        info(this, "Events created in ${System.currentTimeMillis() - _timer} ms")
        _eventRepository.saveAll(
            listOf(
                workingAtTheOffice,
                dailyMeeting,
                universityClasses01,
                universityClasses02,
                universityClasses03,
                universityClasses04,
                gymBicepsAndBackWorkout,
                gymTricepsAndChestWorkout,
                gymCoreWorkout,
                gymLegWorkout,
                birthdayParty
            )
        )
    }

    private fun createTasks(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        _timer = System.currentTimeMillis()

        val walkTheDog = Task(
            title = "Walk the Dog",
            description = "After my morning coffee.",
            status = TaskStatus.TODO,
            calendar = calendars["first"]!!,
            category = categories["personal"],
        )
        val doShopping = Task(
            title = "Do Shopping",
            description = "There is nothing in the fridge.",
            status = TaskStatus.TODO,
            calendar = calendars["first"]!!,
            category = categories["personal"],
        )
        val studyForExam = Task(
            title = "Study for Exam",
            description = "Study for final term exam.",
            status = TaskStatus.IN_PROGRESS,
            calendar = calendars["first"]!!,
            category = categories["university"],
        )

        info(this, "Tasks created in ${System.currentTimeMillis() - _timer} ms")
        _taskRepository.saveAll(listOf(doShopping, walkTheDog, studyForExam))
    }

    private fun createNotes(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        _timer = System.currentTimeMillis()

        val shoppingList = Note(
            title = "Shopping List",
            description = "Milk, Eggs, Bread",
            calendar = calendars["first"]!!,
            category = categories["personal"]
        )

        info(this, "Notes created in ${System.currentTimeMillis() - _timer} ms")
        _noteRepository.save(shoppingList)
    }

}
