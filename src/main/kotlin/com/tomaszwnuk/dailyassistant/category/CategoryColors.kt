package com.tomaszwnuk.dailyassistant.category

import java.awt.Color

object CategoryColors {

    const val DEFAULT = "#1591EA"

    fun toHex(color: Color): String = String.format("#%02x%02x%02x", color.red, color.green, color.blue)

}
