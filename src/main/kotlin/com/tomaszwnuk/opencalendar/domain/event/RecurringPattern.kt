/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.event

enum class RecurringPattern(val value: String) {

    NONE("NONE"),

    DAILY("DAILY"),

    WEEKLY("WEEKLY"),

    MONTHLY("MONTHLY"),

    YEARLY("YEARLY");

}
