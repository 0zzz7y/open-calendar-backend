package com.ozzz7y.opencalendar.domain.category

import java.awt.Color

/**
 * The utility for handling category colors.
 */
object CategoryColorHelper {

    /**
     * The default color for categories.
     */
    const val DEFAULT_COLOR: String = "#1976D2"

    /**
     * Converts a color to a hex string.
     *
     * @param color The color object to convert.
     *
     * @return The hexadecimal string representation of the color.
     */
    fun toHex(color: Color): String = String.format("#%02x%02x%02x", color.red, color.green, color.blue)

}
