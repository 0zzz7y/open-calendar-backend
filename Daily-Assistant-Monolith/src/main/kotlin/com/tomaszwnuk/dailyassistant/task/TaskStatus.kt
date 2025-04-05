package com.tomaszwnuk.dailyassistant.task

enum class TaskStatus(val value: Int) {

    TODO(0),

    IN_PROGRESS(1),

    DONE(2);

    companion object {
        fun valueOf(code: Int): TaskStatus =
            entries.find { it.value == code } ?: throw IllegalArgumentException("Invalid code for TaskStatus: $code")
    }
}
