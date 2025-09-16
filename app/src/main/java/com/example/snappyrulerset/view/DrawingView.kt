package com.example.snappyrulerset.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.snappyrulerset.core.CanvasState
import com.example.snappyrulerset.core.FreehandStroke
import com.example.snappyrulerset.core.Shape
import java.util.UUID

/**
 * Custom View that supports freehand drawing with Bezier curve smoothing.
 * Compose-independent implementation.
 */
class DrawingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var canvasState: CanvasState = CanvasState()

    private val paint: Paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 5f
        isAntiAlias = true
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private var currentPath: Path? = null
    private var lastPoint: PointF? = null
    private var lastMid: PointF? = null

    // Gesture state
    private enum class Mode { NONE, DRAW, TRANSFORM }
    private var mode: Mode = Mode.NONE

    private var startScale: Float = 1f
    private var startOffsetX: Float = 0f
    private var startOffsetY: Float = 0f
    private var startDist: Float = 0f
    private var startMidX: Float = 0f
    private var startMidY: Float = 0f
    private var contentAnchorX: Float = 0f
    private var contentAnchorY: Float = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.translate(canvasState.offsetX, canvasState.offsetY)
        canvas.scale(canvasState.scale, canvasState.scale)

        // Draw saved shapes in content space
        for (shape: Shape in canvasState.shapes) {
            when (shape) {
                is FreehandStroke -> {
                    val oldColor = paint.color
                    val oldWidth = paint.strokeWidth
                    paint.color = shape.color
                    paint.strokeWidth = shape.strokeWidth
                    canvas.drawPath(shape.path, paint)
                    paint.color = oldColor
                    paint.strokeWidth = oldWidth
                }
            }
        }

        // Draw in-progress path in content space
        currentPath?.let { path ->
            canvas.drawPath(path, paint)
        }

        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> onActionDown(event.x, event.y)
            MotionEvent.ACTION_POINTER_DOWN -> if (event.pointerCount >= 2) onSecondFingerDown(event)
            MotionEvent.ACTION_MOVE -> onActionMove(event)
            MotionEvent.ACTION_POINTER_UP -> if (event.pointerCount - 1 < 2 && mode == Mode.TRANSFORM) mode = Mode.NONE
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> onActionUp()
        }
        return true
    }

    private fun onActionDown(x: Float, y: Float) {
        parent?.requestDisallowInterceptTouchEvent(true)
        val (cx, cy) = toContent(x, y)
        currentPath = Path().apply { moveTo(cx, cy) }
        lastPoint = PointF(cx, cy)
        lastMid = PointF(cx, cy)
        mode = Mode.DRAW
        invalidate()
    }

    private fun onActionMove(event: MotionEvent) {
        if (mode == Mode.TRANSFORM && event.pointerCount >= 2) {
            // Transform mode: combined pan + zoom around anchor
            val x0 = event.getX(0)
            val y0 = event.getY(0)
            val x1 = event.getX(1)
            val y1 = event.getY(1)
            val midX = (x0 + x1) / 2f
            val midY = (y0 + y1) / 2f
            val dist = distance(x0, y0, x1, y1)
            if (startDist > 0f) {
                val scaleFactor = dist / startDist
                val newScale = (startScale * scaleFactor).coerceIn(0.2f, 5f)
                canvasState.scale = newScale

                // Keep anchor stable while applying pan from mid movement
                canvasState.offsetX = midX - contentAnchorX * newScale + (midX - startMidX)
                canvasState.offsetY = midY - contentAnchorY * newScale + (midY - startMidY)
            }
            invalidate()
            return
        }

        if (mode == Mode.DRAW) {
            // Use historical points for smoother path on fast motion
            val historySize = event.historySize
            for (i in 0 until historySize) {
                val hx = event.getHistoricalX(i)
                val hy = event.getHistoricalY(i)
                addSmoothedPoint(hx, hy)
            }

            addSmoothedPoint(event.x, event.y)
            invalidate()
        }
    }

    private fun addSmoothedPoint(x: Float, y: Float) {
        val last = lastPoint ?: return
        val (cx, cy) = toContent(x, y)
        val midX = (last.x + cx) / 2f
        val midY = (last.y + cy) / 2f
        val mid = PointF(midX, midY)

        // Quad from previous midpoint to new midpoint, using last point as control
        currentPath?.quadTo(last.x, last.y, mid.x, mid.y)

        lastPoint = PointF(cx, cy)
        lastMid = mid
    }

    private fun onActionUp() {
        if (mode == Mode.DRAW) {
            currentPath?.let { path ->
                val stroke = FreehandStroke(
                    id = UUID.randomUUID().toString(),
                    path = Path(path),
                    color = paint.color,
                    strokeWidth = paint.strokeWidth
                )
                canvasState.shapes.add(stroke)
            }
        }
        currentPath = null
        lastPoint = null
        lastMid = null
        mode = Mode.NONE
        invalidate()
    }

    private fun onSecondFingerDown(event: MotionEvent) {
        // Enter transform mode, initialize anchors and baselines
        mode = Mode.TRANSFORM
        currentPath = null // stop drawing if any

        val x0 = event.getX(0)
        val y0 = event.getY(0)
        val x1 = event.getX(1)
        val y1 = event.getY(1)
        startMidX = (x0 + x1) / 2f
        startMidY = (y0 + y1) / 2f
        startDist = distance(x0, y0, x1, y1)

        startScale = canvasState.scale
        startOffsetX = canvasState.offsetX
        startOffsetY = canvasState.offsetY

        // Content-space anchor under the midpoint at gesture start
        contentAnchorX = (startMidX - startOffsetX) / startScale
        contentAnchorY = (startMidY - startOffsetY) / startScale
    }

    private fun toContent(x: Float, y: Float): Pair<Float, Float> {
        val cx = (x - canvasState.offsetX) / canvasState.scale
        val cy = (y - canvasState.offsetY) / canvasState.scale
        return cx to cy
    }

    private fun distance(x0: Float, y0: Float, x1: Float, y1: Float): Float {
        val dx = x1 - x0
        val dy = y1 - y0
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}


