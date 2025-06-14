package com.tomaszwnuk.opencalendar.domain.field

/**
 * The data constraints for the fields.
 */
object FieldConstraints {

    /**
     * The maximum length of the name field.
     */
    const val NAME_MAXIMUM_LENGTH: Int = 255

    /**
     * The maximum length of the description field.
     */
    const val DESCRIPTION_MAXIMUM_LENGTH: Int = 4096

    /**
     * The maximum length of the emoji field.
     */
    const val EMOJI_MAXIMUM_LENGTH: Int = 255

    /**
     * The length of the color hex code.
     */
    const val COLOR_HEX_LENGTH: Int = 7

    /**
     * The maximum length of the user name field.
     */
    const val USER_NAME_MAXIMUM_LENGTH: Int = 32

    /**
     * The maximum length of the user email field.
     */
    const val USER_EMAIL_MAXIMUM_LENGTH: Int = 255

    /**
     * The minimum length of the user password field.
     */
    const val USER_PASSWORD_MINIMUM_LENGTH: Int = 8

    /**
     * The maximum length of the user password field.
     */
    const val USER_PASSWORD_MAXIMUM_LENGTH: Int = 64

    /**
     * The column definition for the unique identifier field.
     */
    const val COLUMN_DEFINITION_ID: String = "UUID"

    /**
     * The column definition for the name field.
     */
    const val COLUMN_DEFINITION_NAME: String = "VARCHAR(255)"

    /**
     * The column definition for the description field.
     */
    const val COLUMN_DEFINITION_DESCRIPTION: String = "VARCHAR(4096)"

    /**
     * The column definition for the emoji field.
     */
    const val COLUMN_DEFINITION_EMOJI: String = "VARCHAR(255)"

    /**
     * The column definition for the color field.
     */
    const val COLUMN_DEFINITION_COLOR: String = "CHAR(7)"

    /**
     * The column definition for the date field.
     */
    const val COLUMN_DEFINITION_DATE: String = "TIMESTAMP"

    /**
     * The column definition for the recurring pattern field.
     */
    const val COLUMN_DEFINITION_RECURRING_PATTERN: String = "VARCHAR(32)"

    /**
     * The column definition for the task status field.
     */
    const val COLUMN_DEFINITION_TASK_STATUS: String = "VARCHAR(32)"

    /**
     * The column definition for the user name field.
     */
    const val COLUMN_DEFINITION_USER_NAME: String = "VARCHAR(32)"

    /**
     * The column definition for the user email field.
     */
    const val COLUMN_DEFINITION_USER_EMAIL: String = "VARCHAR(255)"

    /**
     * The column definition for the user password field.
     */
    const val COLUMN_DEFINITION_USER_PASSWORD: String = "VARCHAR(64)"

}
