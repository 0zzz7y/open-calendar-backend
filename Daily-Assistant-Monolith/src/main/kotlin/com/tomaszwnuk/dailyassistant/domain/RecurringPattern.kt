package com.tomaszwnuk.dailyassistant.domain

enum class RecurringPattern(val value: Int) {

    NONE(0),

    DAILY(1),

    WEEKLY(2),

    MONTHLY(3),

    YEARLY(4);

    companion object {
        fun valueOf(code: Int): RecurringPattern =
            entries.find { it.value == code }
                ?: throw IllegalArgumentException("Invalid code for RecurringPattern: $code")
    }

}
