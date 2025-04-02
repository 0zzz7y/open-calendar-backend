
data class EventDto (
    @field:NotBlank(message = "Title cannot be empty.")
    val title: String,
    val description: String? = null,
    val date: Date,
    val recurringPattern: RecurringPattern? = null,
    val calendarId: UUID,
    val categoyrId: UUID?
)