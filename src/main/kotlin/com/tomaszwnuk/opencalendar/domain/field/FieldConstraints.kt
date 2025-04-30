/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.field

/**
 * Object containing field constraints used throughout the application.
 * Defines constants for maximum lengths, column definitions, and other field-related constraints.
 */
object FieldConstraints {

    /**
     * The maximum length allowed for a title field.
     */
    const val TITLE_MAXIMUM_LENGTH: Int = 255

    /**
     * The maximum length allowed for a description field.
     */
    const val DESCRIPTION_MAXIMUM_LENGTH: Int = 4096

    /**
     * The maximum length allowed for an emoji field.
     */
    const val EMOJI_MAXIMUM_LENGTH: Int = 255

    /**
     * The fixed length for a color hex code.
     */
    const val COLOR_HEX_LENGTH: Int = 7

    /**
     * The column definition for an ID field in the database.
     */
    const val COLUMN_DEFINITION_ID: String = "CHAR(36)"

    /**
     * The column definition for a title field in the database.
     */
    const val COLUMN_DEFINITION_TITLE: String = "VARCHAR(255)"

    /**
     * The column definition for a description field in the database.
     */
    const val COLUMN_DEFINITION_DESCRIPTION: String = "VARCHAR(4096)"

    /**
     * The column definition for an emoji field in the database.
     */
    const val COLUMN_DEFINITION_EMOJI: String = "VARCHAR(255)"

    /**
     * The column definition for a color field in the database.
     */
    const val COLUMN_DEFINITION_COLOR: String = "CHAR(7)"

    /**
     * The column definition for a date field in the database.
     */
    const val COLUMN_DEFINITION_DATE: String = "TEXT"

}
