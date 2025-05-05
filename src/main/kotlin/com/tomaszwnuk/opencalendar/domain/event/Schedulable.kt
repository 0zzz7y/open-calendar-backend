/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.event

import java.time.LocalDateTime

/**
 * Interface representing a schedulable entity.
 * Defines the properties required for scheduling, including start and end dates, and a recurring pattern.
 */
interface Schedulable {

    /**
     * The start date and time of the schedulable entity.
     * This field is optional and can be null.
     */
    val startDate: LocalDateTime?

    /**
     * The end date and time of the schedulable entity.
     * This field is optional and can be null.
     */
    val endDate: LocalDateTime?

    /**
     * The recurring pattern of the schedulable entity.
     * Specifies how the entity recurs over time.
     */
    val recurringPattern: RecurringPattern

}
