/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.task

/**
 * Enum representing the status of a task.
 * Provides predefined constants for task statuses such as TODO, IN_PROGRESS, and DONE.
 *
 * @property value The string representation of the task status.
 */
enum class TaskStatus(val value: String) {

    /**
     * Represents a task that is yet to be started.
     */
    TODO("TODO"),

    /**
     * Represents a task that is currently in progress.
     */
    IN_PROGRESS("IN_PROGRESS"),

    /**
     * Represents a task that has been completed.
     */
    DONE("DONE");

}
