@file:Suppress("unused")

package com.tomaszwnuk.dailyassistant.domain.utility

import org.slf4j.Logger
import org.slf4j.LoggerFactory

inline fun <reified T> T.logger(): Logger {
    return LoggerFactory.getLogger(T::class.java)
}

fun info(source: Any, message: String) {
    LoggerFactory.getLogger(source::class.java).info(message)
}

fun warn(source: Any, message: String) {
    LoggerFactory.getLogger(source::class.java).warn(message)
}

fun error(source: Any, message: String) {
    LoggerFactory.getLogger(source::class.java).error(message)
}
