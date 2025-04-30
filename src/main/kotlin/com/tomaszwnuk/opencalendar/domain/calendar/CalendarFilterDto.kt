/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.calendar

/**
 * Data Transfer Object (DTO) for filtering calendars.
 * Used to specify criteria for filtering calendar entities.
 *
 * @property title The title of the calendar to filter by (optional).
 * @property emoji The emoji of the calendar to filter by (optional).
 */
class CalendarFilterDto(

    /**
     * The title of the calendar to filter by.
     * This field is optional and can be null.
     */
    val title: String? = null,

    /**
     * The emoji of the calendar to filter by.
     * This field is optional and can be null.
     */
    val emoji: String? = null

)
