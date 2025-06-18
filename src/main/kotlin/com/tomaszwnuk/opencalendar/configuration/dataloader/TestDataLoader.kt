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
import com.tomaszwnuk.opencalendar.domain.user.User
import com.tomaszwnuk.opencalendar.domain.user.UserRepository
import com.tomaszwnuk.opencalendar.utility.logger.info
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

/**
 * The test data loader for populating the database with initial data.
 */
@Suppress("unused")
@Profile("production", "development", "test")
@Component
class TestDataLoader(

    /**
     * The repositories for managing calendar data.
     */
    private val _calendarRepository: CalendarRepository,

    /**
     * The repositories for managing category data.
     */
    private val _categoryRepository: CategoryRepository,

    /**
     * The repositories for managing event data.
     */
    private val _eventRepository: EventRepository,

    /**
     * The repositories for managing task data.
     */
    private val _taskRepository: TaskRepository,

    /**
     * The repositories for managing note data.
     */
    private val _noteRepository: NoteRepository,

    /**
     * The repository for managing user data.
     */
    private val _userRepository: UserRepository

) : CommandLineRunner {

    /**
     * The timer for measuring the time of operations.
     */
    private var _timer: Long = System.currentTimeMillis()

    /**
     * The user for whom the test data is created.
     */
    private lateinit var _user: User

    /**
     * Runs the data loader to populate the database with test data.
     *
     * @param arguments The command line arguments (not used)
     */
    override fun run(vararg arguments: String?) {
        if (_calendarRepository.count() > 0) {
            info(source = this, message = "Test data already loaded. Skipping...")
            return
        }
        _timer = System.currentTimeMillis()

        createUsers()
        val calendars: Map<String, Calendar> = createCalendars()
        val categories: Map<String, Category> = createCategories()

        createNotes(calendars, categories)
        createTasks(calendars, categories)
        createEvents(calendars, categories)

        info(source = this, message = "Test data loaded in ${System.currentTimeMillis() - _timer} ms")
    }

    /**
     * Creates a test users for whom the data will be created.
     */
    private fun createUsers() {
        _timer = System.currentTimeMillis()

        _user = User(
            id = UUID.randomUUID(),
            username = "test",
            email = "test@email.com",
            password = BCryptPasswordEncoder().encode("password")
        )
        _userRepository.save(_user)

        info(source = this, message = "User created in ${System.currentTimeMillis() - _timer} ms")
    }

    /**
     * Creates test calendars.
     *
     * @return A map of created calendars
     */
    private fun createCalendars(): Map<String, Calendar> {
        _timer = System.currentTimeMillis()

        val first: Calendar = _calendarRepository.save(
            Calendar(
                id = UUID.randomUUID(),
                name = "#1",
                emoji = "\uD83D\uDCA5",
                _user.id
            )
        )
        val calendars: Map<String, Calendar> = mapOf("first" to first)

        info(source = this, message = "Calendars created in ${System.currentTimeMillis() - _timer} ms")
        return calendars
    }

    /**
     * Creates test categories.
     *
     * @return A map of created categories
     */
    private fun createCategories(): Map<String, Category> {
        _timer = System.currentTimeMillis()

        val personal: Category = _categoryRepository.save(
            Category(
                name = "Personal",
                color = "#EFEF39",
                userId = _user.id
            )
        )
        val work: Category = _categoryRepository.save(
            Category(
                name = "Work",
                color = "#48DD52",
                userId = _user.id
            )
        )
        val university: Category = _categoryRepository.save(
            Category(
                name = "University",
                color = "#E8475D",
                userId = _user.id
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

    /**
     * Creates test events.
     *
     * @param calendars A map of calendars to associate with the events
     * @param categories A map of categories to associate with the events
     */
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

    /**
     * Creates test tasks.
     *
     * @param calendars A map of calendars to associate with the tasks
     * @param categories A map of categories to associate with the tasks
     */
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

    /**
     * Creates test notes.
     *
     * @param calendars A map of calendars to associate with the notes
     * @param categories A map of categories to associate with the notes
     */
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
