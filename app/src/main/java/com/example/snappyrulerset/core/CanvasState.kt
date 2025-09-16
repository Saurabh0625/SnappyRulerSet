package com.example.snappyrulerset.core

/**
 * Holds the list of shapes rendered on the drawing canvas.
 */
data class CanvasState(
    val shapes: MutableList<Shape> = mutableListOf(),
    var scale: Float = 1f,
    var offsetX: Float = 0f,
    var offsetY: Float = 0f
)


