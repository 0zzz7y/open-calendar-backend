/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.communication

/**
 * Object containing constants related to communication within the application.
 * Provides URLs for the frontend in different environments.
 */
object CommunicationConstants {

    /**
     * The production URL of the frontend application.
     */
    const val FRONTEND_PRODUCTION_URL: String = "https://open-calendar-frontend.onrender.com"

    /**
     * The development URL of the frontend application.
     */
    const val FRONTEND_DEVELOPMENT_URL: String = "http://localhost:5173"

}
