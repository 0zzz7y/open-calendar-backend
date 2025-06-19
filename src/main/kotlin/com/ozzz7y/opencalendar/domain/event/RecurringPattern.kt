package com.ozzz7y.opencalendar.domain.event

/**
 * The recurring pattern of an event.
 */
enum class RecurringPattern(val value: String) {

    /**
     * The event does not recur.
     */
    NONE("NONE"),

    /**
     * The event recurs daily.
     */
    DAILY("DAILY"),

    /**
     * The event recurs weekly.
     */
    WEEKLY("WEEKLY"),

    /**
     * The event recurs monthly.
     */
    MONTHLY("MONTHLY"),

    /**
     * The event recurs yearly.
     */
    YEARLY("YEARLY");

}
