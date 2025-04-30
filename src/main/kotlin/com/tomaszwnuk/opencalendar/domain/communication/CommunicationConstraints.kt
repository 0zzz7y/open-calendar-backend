/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.communication

/**
 * Object containing constraints related to communication within the application.
 * Defines limits for request size and the number of requests per minute.
 */
@Suppress("unused")
object CommunicationConstraints {

    /**
     * The maximum size of a request in bytes.
     * Set to 1 MB (1024 * 1024 bytes).
     */
    const val MAXIMUM_REQUEST_SIZE: Long = 1024 * 1024

    /**
     * The maximum number of requests allowed per minute.
     * Set to 1024 requests.
     */
    const val MAXIMUM_REQUESTS_PER_MINUTE: Long = 1024

}
