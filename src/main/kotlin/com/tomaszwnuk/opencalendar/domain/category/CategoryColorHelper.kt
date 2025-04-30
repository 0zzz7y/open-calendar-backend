package com.tomaszwnuk.opencalendar.domain.category

import java.awt.Color

object CategoryColorHelper {

    const val DEFAULT_COLOR: String = "#1976D2"

    fun toHex(color: Color): String = String.format("#%02x%02x%02x", color.red, color.green, color.blue)

}
