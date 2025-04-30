/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.event

import com.tomaszwnuk.opencalendar.domain.other.RecurringPattern
import java.time.LocalDateTime
import java.util.*

/**
 * Data Transfer Object (DTO) for filtering events.
 * Provides optional fields to specify filtering criteria for events.
 *
 * @property title The title of the event to filter by (optional).
 * @property description The description of the event to filter by (optional).
 * @property dateFrom The start date for filtering events (optional).
 * @property dateTo The end date for filtering events (optional).
 * @property recurringPattern The recurring pattern of the event to filter by (optional).
 * @property calendarId The ID of the calendar to filter events by (optional).
 * @property categoryId The ID of the category to filter events by (optional).
 */
data class EventFilterDto(

    /**
     * The title of the event to filter by.
     * This field is optional.
     */
    val title: String? = null,

    /**
     * The description of the event to filter by.
     * This field is optional.
     */
    val description: String? = null,

    /**
     * The start date for filtering events.
     * This field is optional.
     */
    val dateFrom: LocalDateTime? = null,

    /**
     * The end date for filtering events.
     * This field is optional.
     */
    val dateTo: LocalDateTime? = null,

    /**
     * The recurring pattern of the event to filter by.
     * This field is optional.
     */
    val recurringPattern: RecurringPattern? = null,

    /**
     * The ID of the calendar to filter events by.
     * This field is optional.
     */
    val calendarId: UUID? = null,

    /**
     * The ID of the category to filter events by.
     * This field is optional.
     */
    val categoryId: UUID? = null

)
