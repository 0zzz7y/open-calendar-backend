
@Service
class EventService(
    private val _eventRepository: EventRepository,
    private val _calendarRepository: CalendarRepository,
    private val _categoryRepository: CategoryRepository
) {
    fun create(eventDto: EventDto): Event {
        val calendar: Calendar = eventDto.calendarId?.let { _calendarRepository.findById(it).orElse(null) }
        val category: Category = eventDto.categoryId?.let { _categoryRepository.findById(it).orElse(null) }
        val event: Event = Event(
            title = eventDto.title,
            description = eventDto.description,
            date = eventDto.date,
            recurringPattern = eventDto.recurringPattern,
            category = category,
            calendar = calendar
        )
        return _eventRepository.save(event)
    }
}