package com.tomaszwnuk.dailyassistant.validation

object FieldConstraints {

    const val NAME_MAXIMUM_LENGTH: Int = 255

    const val DESCRIPTION_MAXIMUM_LENGTH: Int = 4096

    const val EMOJI_MAXIMUM_LENGTH: Int = 255

    const val COLOR_HEX_LENGTH: Int = 7

    const val COLUMN_DEFINITION_ID: String = "CHAR(36)"

    const val COLUMN_DEFINITION_NAME: String = "VARCHAR(255)"

    const val COLUMN_DEFINITION_DESCRIPTION: String = "VARCHAR(4096)"

    const val COLUMN_DEFINITION_EMOJI: String = "VARCHAR(255)"

    const val COLUMN_DEFINITION_COLOR: String = "CHAR(7)"

    const val COLUMN_DEFINITION_DATE: String = "TIMESTAMP"

}
