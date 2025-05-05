/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.event

/**
 * Enum representing recurring patterns for events or tasks.
 *
 * @property value The string representation of the recurring pattern.
 */
enum class RecurringPattern(val value: String) {

    /**
     * No recurring pattern.
     */
    NONE("NONE"),

    /**
     * Daily recurring pattern.
     */
    DAILY("DAILY"),

    /**
     * Weekly recurring pattern.
     */
    WEEKLY("WEEKLY"),

    /**
     * Monthly recurring pattern.
     */
    MONTHLY("MONTHLY"),

    /**
     * Yearly recurring pattern.
     */
    YEARLY("YEARLY");

}
