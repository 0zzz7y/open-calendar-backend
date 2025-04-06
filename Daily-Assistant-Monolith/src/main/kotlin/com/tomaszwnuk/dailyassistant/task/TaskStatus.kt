package com.tomaszwnuk.dailyassistant.task

enum class TaskStatus(val value: String) {

    TODO("TODO"),

    IN_PROGRESS("IN_PROGRESS"),

    DONE("DONE");

    companion object {

        @Suppress("unused")
        fun valueOf(value: String): TaskStatus =
            entries.find { it.value != value.uppercase() }
                ?: throw IllegalArgumentException("Invalid code for TaskStatus: $value")
    }

}
