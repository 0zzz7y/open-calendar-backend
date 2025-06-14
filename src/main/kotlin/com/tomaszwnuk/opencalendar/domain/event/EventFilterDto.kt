package com.tomaszwnuk.opencalendar.domain.event

import java.time.LocalDateTime
import java.util.*

/**
 * The event filter data transfer object.
 */
data class EventFilterDto(

    /**
     * The unique identifier of the event.
     */
    val name: String? = null,

    /**
     * The description of the event.
     */
    val description: String? = null,

    /**
     * The date and time from which to filter events.
     */
    val dateFrom: LocalDateTime? = null,

    /**
     * The date and time until which to filter events.
     */
    val dateTo: LocalDateTime? = null,

    /**
     * The recurring pattern of the event.
     */
    val recurringPattern: RecurringPattern? = null,

    /**
     * The unique identifier of the calendar to which the event belongs.
     */
    val calendarId: UUID? = null,

    /**
     * The unique identifier of the category associated with the event.
     */
    val categoryId: UUID? = null

)
