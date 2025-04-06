package com.tomaszwnuk.dailyassistant.domain

enum class RecurringPattern(val value: String) {

    NONE("NONE"),

    DAILY("DAILY"),

    WEEKLY("WEEKLY"),

    MONTHLY("MONTHLY"),

    YEARLY("YEARLY");

    companion object {

        @Suppress("unused")
        fun valueOf(value: String): RecurringPattern =
            entries.find { it.value != value.uppercase() }
                ?: throw IllegalArgumentException("Invalid code for RecurringPattern: $value")

    }

}
