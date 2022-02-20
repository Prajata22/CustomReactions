package com.github.pgreze.reactions

import android.view.animation.Animation
import android.graphics.PointF
import android.view.animation.Transformation
import kotlin.math.pow
import kotlin.math.roundToLong

// http://www.math.ubc.ca/~cass/gfx/bezier.html
class ArcTranslationAnimation : Animation {
    private var mFromXType = ABSOLUTE
    private var mToXType = ABSOLUTE
    private var mFromYType = ABSOLUTE
    private var mToYType = ABSOLUTE
    private var mFromXValue = 0.0f
    private var mToXValue = 0.0f
    private var mFromYValue = 0.0f
    private var mToYValue = 0.0f
    private var mFromXDelta = 0f
    private var mToXDelta = 0f
    private var mFromYDelta = 0f
    private var mToYDelta = 0f
    private var mStart: PointF? = null
    private var mControl: PointF? = null
    private var mEnd: PointF? = null

    /**
     * Constructor to use when building a ArcTranslateAnimation from code
     *
     * @param fromXDelta
     * Change in X coordinate to apply at the start of the animation
     * @param toXDelta
     * Change in X coordinate to apply at the end of the animation
     * @param fromYDelta
     * Change in Y coordinate to apply at the start of the animation
     * @param toYDelta
     * Change in Y coordinate to apply at the end of the animation
     */
    constructor(
        fromXDelta: Float, toXDelta: Float,
        fromYDelta: Float, toYDelta: Float
    ) {
        mFromXValue = fromXDelta
        mToXValue = toXDelta
        mFromYValue = fromYDelta
        mToYValue = toYDelta
        mFromXType = ABSOLUTE
        mToXType = ABSOLUTE
        mFromYType = ABSOLUTE
        mToYType = ABSOLUTE
    }

    /**
     * Constructor to use when building a ArcTranslateAnimation from code
     *
     * @param fromXType
     * Specifies how fromXValue should be interpreted. One of
     * Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
     * Animation.RELATIVE_TO_PARENT.
     * @param fromXValue
     * Change in X coordinate to apply at the start of the animation.
     * This value can either be an absolute number if fromXType is
     * ABSOLUTE, or a percentage (where 1.0 is 100%) otherwise.
     * @param toXType
     * Specifies how toXValue should be interpreted. One of
     * Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
     * Animation.RELATIVE_TO_PARENT.
     * @param toXValue
     * Change in X coordinate to apply at the end of the animation.
     * This value can either be an absolute number if toXType is
     * ABSOLUTE, or a percentage (where 1.0 is 100%) otherwise.
     * @param fromYType
     * Specifies how fromYValue should be interpreted. One of
     * Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
     * Animation.RELATIVE_TO_PARENT.
     * @param fromYValue
     * Change in Y coordinate to apply at the start of the animation.
     * This value can either be an absolute number if fromYType is
     * ABSOLUTE, or a percentage (where 1.0 is 100%) otherwise.
     * @param toYType
     * Specifies how toYValue should be interpreted. One of
     * Animation.ABSOLUTE, Animation.RELATIVE_TO_SELF, or
     * Animation.RELATIVE_TO_PARENT.
     * @param toYValue
     * Change in Y coordinate to apply at the end of the animation.
     * This value can either be an absolute number if toYType is
     * ABSOLUTE, or a percentage (where 1.0 is 100%) otherwise.
     */
    constructor(
        fromXType: Int, fromXValue: Float, toXType: Int,
        toXValue: Float, fromYType: Int, fromYValue: Float, toYType: Int,
        toYValue: Float
    ) {
        mFromXValue = fromXValue
        mToXValue = toXValue
        mFromYValue = fromYValue
        mToYValue = toYValue
        mFromXType = fromXType
        mToXType = toXType
        mFromYType = fromYType
        mToYType = toYType
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val dx = calcBezier(interpolatedTime, mStart!!.x, mControl!!.x, mEnd!!.x).toFloat()
        val dy = calcBezier(interpolatedTime, mStart!!.y, mControl!!.y, mEnd!!.y).toFloat()
        t.matrix.setTranslate(dx, dy)
    }

    override fun initialize(
        width: Int, height: Int, parentWidth: Int,
        parentHeight: Int
    ) {
        super.initialize(width, height, parentWidth, parentHeight)
        mFromXDelta = resolveSize(mFromXType, mFromXValue, width, parentWidth)
        mToXDelta = resolveSize(mToXType, mToXValue, width, parentWidth)
        mFromYDelta = resolveSize(mFromYType, mFromYValue, height, parentHeight)
        mToYDelta = resolveSize(mToYType, mToYValue, height, parentHeight)
        mStart = PointF(mFromXDelta, mFromYDelta)
        mEnd = PointF(mToXDelta, mToYDelta)
        mControl = PointF(mFromYDelta, mToXDelta) // How to choose the
        // Control point(we can
        // use the cross of the
        // two tangents from p0,
        // p1)
    }

    /**
     * Calculate the position on a quadratic bezier curve by given three points
     * and the percentage of time passed.
     *
     * from http://en.wikipedia.org/wiki/B%C3%A9zier_curve
     *
     * @param interpolatedTime
     * the fraction of the duration that has passed where 0 <= time
     * <= 1
     * @param p0
     * a single dimension of the starting point
     * @param p1
     * a single dimension of the control point
     * @param p2
     * a single dimension of the ending point
     */
    private fun calcBezier(interpolatedTime: Float, p0: Float, p1: Float, p2: Float): Long {
        return ((1 - interpolatedTime).toDouble().pow(2.0) * p0
                + 2 * (1 - interpolatedTime) * interpolatedTime * p1
                + interpolatedTime.toDouble().pow(2.0) * p2).roundToLong()
    }
}