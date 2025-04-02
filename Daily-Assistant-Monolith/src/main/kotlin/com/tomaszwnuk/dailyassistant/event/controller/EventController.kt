
@RequestController
@RequestMapping("/events")
class EventController(
    private val _eventService: EventService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody eventDto: EventDto): Event {
        _eventService.create(eventDto)
    }
}