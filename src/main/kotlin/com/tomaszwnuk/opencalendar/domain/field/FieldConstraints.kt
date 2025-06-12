package com.tomaszwnuk.opencalendar.domain.field

object FieldConstraints {

    const val NAME_MAXIMUM_LENGTH: Int = 255

    const val DESCRIPTION_MAXIMUM_LENGTH: Int = 4096

    const val EMOJI_MAXIMUM_LENGTH: Int = 255

    const val COLOR_HEX_LENGTH: Int = 7

    const val USER_NAME_MAXIMUM_LENGTH: Int = 32

    const val USER_EMAIL_MAXIMUM_LENGTH: Int = 255

    const val USER_PASSWORD_MINIMUM_LENGTH: Int = 8

    const val USER_PASSWORD_MAXIMUM_LENGTH: Int = 64

    const val COLUMN_DEFINITION_ID: String = "CHAR(36)"

    const val COLUMN_DEFINITION_NAME: String = "VARCHAR(255)"

    const val COLUMN_DEFINITION_DESCRIPTION: String = "VARCHAR(4096)"

    const val COLUMN_DEFINITION_EMOJI: String = "VARCHAR(255)"

    const val COLUMN_DEFINITION_COLOR: String = "CHAR(7)"

    const val COLUMN_DEFINITION_DATE: String = "TIMESTAMP"

    const val COLUMN_DEFINITION_USER_NAME: String = "VARCHAR(32)"

    const val COLUMN_DEFINITION_USER_EMAIL: String = "VARCHAR(255)"

    const val COLUMN_DEFINITION_USER_PASSWORD: String = "VARCHAR(64)"

}
