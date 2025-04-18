package com.tomaszwnuk.dailyassistant.configuration

import com.tomaszwnuk.dailyassistant.calendar.Calendar
import com.tomaszwnuk.dailyassistant.calendar.CalendarRepository
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.category.CategoryColors
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
import java.awt.Color
import java.time.LocalDateTime

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
        val personal: Calendar = _calendarRepository.save(Calendar(name = "Personal", emoji = "🏠"))
        val work: Calendar = _calendarRepository.save(Calendar(name = "Work", emoji = "💼"))
        val calendars: Map<String, Calendar> = mapOf(
            "personal" to personal,
            "work" to work
        )

        info(this, "Calendars created in ${System.currentTimeMillis() - startTime} ms")
        return calendars
    }

    private fun createCategories(): Map<String, Category> {
        val startTime: Long = System.currentTimeMillis()
        val urgent: Category =
            _categoryRepository.save(Category(name = "Urgent", color = CategoryColors.toHex(Color.RED)))
        val casual: Category =
            _categoryRepository.save(Category(name = "Casual", color = CategoryColors.toHex(Color.GREEN)))
        val health: Category =
            _categoryRepository.save(Category(name = "Health", color = CategoryColors.toHex(Color.RED)))
        val categories: Map<String, Category> = mapOf(
            "urgent" to urgent,
            "casual" to casual,
            "health" to health
        )

        info(this, "Categories created in ${System.currentTimeMillis() - startTime} ms")
        return categories
    }

    private fun createEvents(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        val startTime: Long = System.currentTimeMillis()
        val now: LocalDateTime = LocalDateTime.now()
        val dailyStandup = Event(
            name = "Daily Standup",
            description = "Team sync-up",
            startDate = now.withHour(9),
            endDate = now.withHour(9).plusMinutes(30),
            calendar = calendars["work"]!!,
            category = categories["urgent"],
            recurringPattern = RecurringPattern.DAILY
        )
        val birthdayParty = Event(
            name = "Birthday Party",
            description = "Friend's birthday celebration",
            startDate = now.plusDays(10).withHour(18),
            endDate = now.plusDays(10).withHour(23),
            calendar = calendars["personal"]!!,
            category = categories["casual"],
            recurringPattern = RecurringPattern.YEARLY
        )

        info(this, "Events created in ${System.currentTimeMillis() - startTime} ms")
        _eventRepository.saveAll(listOf(dailyStandup, birthdayParty))
    }

    private fun createTasks(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        val startTime: Long = System.currentTimeMillis()
        val now: LocalDateTime = LocalDateTime.now()
        val bicepsAndBackTraining = Task(
            name = "Biceps & Back",
            description = "Pull day training session",
            startDate = now.plusDays(1).withHour(17),
            endDate = now.plusDays(1).withHour(18),
            calendar = calendars["personal"]!!,
            category = categories["health"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val tricepsAndChestTraining = Task(
            name = "Triceps & Chest",
            description = "Push day training session",
            startDate = now.plusDays(2).withHour(17),
            endDate = now.plusDays(2).withHour(18),
            calendar = calendars["personal"]!!,
            category = categories["health"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val coreTraining = Task(
            name = "Core",
            description = "Abdominal workout",
            startDate = now.plusDays(4).withHour(17),
            endDate = now.plusDays(4).withHour(18),
            calendar = calendars["personal"]!!,
            category = categories["health"],
            recurringPattern = RecurringPattern.WEEKLY
        )
        val legsTraining = Task(
            name = "Legs",
            description = "Squats, Lunges, Leg press",
            startDate = now.plusDays(5).withHour(17),
            endDate = now.plusDays(5).withHour(18),
            calendar = calendars["personal"]!!,
            category = categories["health"],
            recurringPattern = RecurringPattern.WEEKLY
        )

        info(this, "Tasks created in ${System.currentTimeMillis() - startTime} ms")
        _taskRepository.saveAll(listOf(bicepsAndBackTraining, tricepsAndChestTraining, coreTraining, legsTraining))
    }

    private fun createNotes(calendars: Map<String, Calendar>, categories: Map<String, Category>) {
        val startTime: Long = System.currentTimeMillis()
        val shoppingList = Note(
            name = "Shopping List",
            description = "Milk, Eggs, Bread",
            calendar = calendars["personal"]!!,
            category = categories["casual"]
        )

        info(this, "Notes created in ${System.currentTimeMillis() - startTime} ms")
        _noteRepository.save(shoppingList)
    }

}
