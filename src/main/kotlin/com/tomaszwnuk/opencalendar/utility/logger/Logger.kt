@file:Suppress("unused")

package com.tomaszwnuk.opencalendar.utility.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Extension function to get a logger for the class of the object.
 *
 * @return Logger instance for the class of the object
 */
inline fun <reified T> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

/**
 * Logs an info message using the logger for the class of the object.
 *
 * @param source The source object for which the logger is created
 * @param message The message to log
 */
fun info(source: Any, message: String) {
    LoggerFactory.getLogger(source::class.java).info(message)
}

/**
 * Logs a debug message using the logger for the class of the object.
 *
 * @param source The source object for which the logger is created
 * @param message The message to log
 */
fun warn(source: Any, message: String) {
    LoggerFactory.getLogger(source::class.java).warn(message)
}

/**
 * Logs an error message using the logger for the class of the object.
 *
 * @param source The source object for which the logger is created
 * @param message The message to log
 */
fun error(source: Any, message: String) {
    LoggerFactory.getLogger(source::class.java).error(message)
}
