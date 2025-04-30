/*
 * Copyright (c) Tomasz Wnuk
 */

package com.tomaszwnuk.opencalendar.domain.category

import java.awt.Color

/**
 * Helper object for managing category colors.
 * Provides utility methods and constants related to color handling.
 */
object CategoryColorHelper {

    /**
     * The default color for categories, represented as a hexadecimal string.
     */
    const val DEFAULT_COLOR: String = "#1976D2"

    /**
     * Converts a `Color` object to its hexadecimal string representation.
     *
     * @param color The `Color` object to convert.
     *
     * @return The hexadecimal string representation of the color.
     */
    fun toHex(color: Color): String = String.format("#%02x%02x%02x", color.red, color.green, color.blue)

}
