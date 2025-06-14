package com.tomaszwnuk.opencalendar.domain.event

import java.time.LocalDateTime

/**
 * The entity that can be scheduled.
 */
interface Schedulable {

    /**
     * The start date and time of the schedulable entity.
     */
    val startDate: LocalDateTime?

    /**
     * The end date and time of the schedulable entity.
     */
    val endDate: LocalDateTime?

    /**
     * The recurring pattern of the schedulable entity.
     */
    val recurringPattern: RecurringPattern

}
