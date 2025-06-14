package com.tomaszwnuk.opencalendar.domain.task

/**
 * The status of a task.
 */
enum class TaskStatus(val value: String) {

    /**
     * The task is yet to be started.
     */
    TODO("TODO"),

    /**
     * The task is currently in progress.
     */
    IN_PROGRESS("IN_PROGRESS"),

    /**
     * The task has been completed.
     */
    DONE("DONE");

}
