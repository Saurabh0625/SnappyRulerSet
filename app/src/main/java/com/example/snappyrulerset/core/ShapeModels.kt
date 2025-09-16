package com.example.snappyrulerset.core

import android.graphics.Color
import android.graphics.Path

/**
 * Base type for all drawable shapes on the canvas.
 */
sealed class Shape

/**
 * Freehand stroke represented by an Android Path and render attributes.
 */
data class FreehandStroke(
    val id: String,
    val path: Path,
    val color: Int = Color.BLACK,
    val strokeWidth: Float = 5f
) : Shape()


