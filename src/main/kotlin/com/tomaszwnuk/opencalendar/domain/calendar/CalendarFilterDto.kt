package com.tomaszwnuk.opencalendar.domain.calendar

/**
 * The calendar filter data transfer object.
 */
class CalendarFilterDto(

    /**
     * The name of the calendar to filter by.
     */
    val name: String? = null,

    /**
     * The emoji representing the calendar to filter by.
     */
    val emoji: String? = null

)
