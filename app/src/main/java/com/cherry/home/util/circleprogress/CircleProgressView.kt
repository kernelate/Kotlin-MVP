package com.cherry.home.util.circleprogress

import android.animation.TimeInterpolator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Paint.Style
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.Typeface
import android.os.Build
import android.os.Message
import android.support.annotation.ColorInt
import android.support.annotation.FloatRange
import android.support.annotation.IntRange
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.cherry.home.util.circleprogress.ColorUtils

import com.cherry.home.R

import java.text.DecimalFormat

/**
 * An circle view, similar to Android's ProgressBar.
 * Can be used in 'value mode' or 'spinning mode'.
 *
 *
 * In spinning mode it can be used like a intermediate progress bar.
 *
 *
 * In value mode it can be used as a progress bar or to visualize any other value.
 * Setting a value is fully animated. There are also nice transitions from animating to value mode.
 *
 *
 * Typical use case would be to load a new value. During the loading time set the CircleView to spinning.
 * As soon as you get your value, just set it with [.setValueAnimated].
 *
 * @author Jakob Grabner, based on the Progress wheel of Todd Davies
 * https://github.com/Todd-Davies/CircleView
 *
 *
 * Licensed under the Creative Commons Attribution 3.0 license see:
 * http://creativecommons.org/licenses/by/3.0/
 */
class CircleProgressView
//endregion getter/setter
//----------------------------------

/**
 * The constructor for the CircleView
 *
 * @param context The context.
 * @param attrs   The attributes.
 */
(context: Context, attrs: AttributeSet) : View(context, attrs) {
    //----------------------------------
    //region members

    //value animation
    internal var mCurrentValue = 42f
    internal var mValueTo = 0f
    internal var mValueFrom = 0f
    /**
     * The max value of the progress bar. Used to calculate the percentage of the current value.
     * The bar fills according to the percentage. The default value is 100.
     *
     * @param _maxValue The max value.
     */
    var maxValue = 100f

    // spinner animation
    internal var mSpinningBarLengthCurrent = 0f
    internal var mSpinningBarLengthOrig = 42f
    internal var mCurrentSpinnerDegreeValue = 0f
    //Animation
    //The amount of degree to move the bar by on each draw
    /**
     * The amount of degree to move the bar on every draw call.
     *
     * @param spinSpeed the speed of the spinner
     */
    var spinSpeed = 2.8f
    /**
     * The animation duration in ms
     */
    internal var mAnimationDuration = 900.0
    //The number of milliseconds to wait in between each draw
    /**
     * @return The number of ms to wait between each draw call.
     */
    /**
     * @param delayMillis The number of ms to wait between each draw call.
     */
    var delayMillis = 10
    // helper for AnimationState.END_SPINNING_START_ANIMATING
    internal var mDrawBarWhileSpinning: Boolean = false
    //The animation handler containing the animation state machine.
    internal var mAnimationHandler = AnimationHandler(this)
    //The current state of the animation state machine.
    internal var mAnimationState = AnimationState.IDLE
    internal var mAnimationStateChangedListener: AnimationStateChangedListener? = null
    protected var mLayoutHeight = 0
    protected var mLayoutWidth = 0
    /**
     * @param barWidth The width of the progress bar in pixel.
     */
    var barWidth = 40
        set(@FloatRange(from = 0.0) barWidth) {
            field = barWidth
            mBarPaint.setStrokeWidth(barWidth.toFloat())
            mBarSpinnerPaint.setStrokeWidth(barWidth.toFloat())
        }
    /**
     * @param rimWidth The width in pixel of the rim around the circle
     */
    var rimWidth = 40
        set(@IntRange(from = 0) rimWidth) {
            field = rimWidth
            mRimPaint.setStrokeWidth(rimWidth.toFloat())
        }
    // get a angle between 0 and 360
    var startAngle = 270
        set(_startAngle) {
            field = normalizeAngle(_startAngle.toFloat()).toInt()
        }
    /**
     * @param _contourSize The size of the background contour of the circle.
     */
    var contourSize = 1f
        set(@FloatRange(from = 0.0) _contourSize) {
            field = _contourSize
            mContourPaint.setStrokeWidth(_contourSize)
        }
    //Default text sizes
    /**
     * Text size of the unit string. Only used if text size is also set. (So automatic text size
     * calculation is off. see [.setTextSize]).
     * If auto text size is on, use [.setUnitScale] to scale unit size.
     *
     * @param unitSize The text size of the unit.
     */
    var unitSize = 10
        set(@IntRange(from = 0) unitSize) {
            field = unitSize
            mUnitTextPaint.setTextSize(unitSize.toFloat())
        }
    /**
     * Text size of the text string. Disables auto text size
     * If auto text size is on, use [.setTextScale] to scale textSize.
     *
     * @param textSize The text size of the unit.
     */
    var textSize = 10
        set(@IntRange(from = 0) textSize) {
            this.mTextPaint.setTextSize(textSize.toFloat())
            field = textSize
            isAutoTextSize = false
        }
    //Text scale
    /**
     * @return The scale value
     */
    /**
     * Scale factor for main text in the center of the circle view.
     * Only used if auto text size is enabled.
     *
     * @param _textScale The scale value.
     */
    var textScale = 1f
    /**
     * @return The scale value
     */
    /**
     * Scale factor for unit text next to the main text.
     * Only used if auto text size is enabled.
     *
     * @param _unitScale The scale value.
     */
    var unitScale = 1f
    //Colors (with defaults)
    private val mBarColorStandard = -0xff6978 //stylish blue
    /**
     * @param _contourColor The color of the background contour of the circle.
     */
    var contourColor = -0x56000000
        set(@ColorInt _contourColor) {
            field = _contourColor
            mContourPaint.setColor(_contourColor)
        }
    private var mSpinnerColor = mBarColorStandard //stylish blue
    private var mBackgroundCircleColor = 0x00000000  //transparent
    /**
     * @param rimColor The color of the rim around the Circle.
     */
    var rimColor = -0x557c2f37
        set(@ColorInt rimColor) {
            field = rimColor
            mRimPaint.setColor(rimColor)
        }
    private var mTextColor = -0x1000000
    private var mUnitColor = -0x1000000
    private var mIsAutoColorEnabled = false
    //endregion members
    //----------------------------------

    //----------------------------------
    //region getter/setter
    //stylish blue
    var barColors = intArrayOf(mBarColorStandard)
        private set
    //Caps
    /**
     * @param _barStrokeCap The stroke cap of the progress bar.
     */
    var barStrokeCap: Paint.Cap = Paint.Cap.BUTT
        set(_barStrokeCap) {
            field = _barStrokeCap
            mBarPaint.setStrokeCap(_barStrokeCap)
        }
    /**
     * @param _spinnerStrokeCap The stroke cap of the progress bar in spinning mode.
     */
    var spinnerStrokeCap: Paint.Cap = Paint.Cap.BUTT
        set(_spinnerStrokeCap) {
            field = _spinnerStrokeCap
            mBarSpinnerPaint.setStrokeCap(_spinnerStrokeCap)
        }
    //Paints
    private val mBarPaint = Paint()
    private val mBarSpinnerPaint = Paint()
    private val mBackgroundCirclePaint = Paint()
    private val mRimPaint = Paint()
    private val mTextPaint = Paint()
    private val mUnitTextPaint = Paint()
    private val mContourPaint = Paint()
    //Rectangles
    protected var mCircleBounds = RectF()
    protected var mInnerCircleBound = RectF()
    protected var mCenter: PointF? = null
    /**
     * Maximum size of the text.
     */
    protected var mOuterTextBounds = RectF()
    /**
     * Actual size of the text.
     */
    protected var mActualTextBounds = RectF()
    protected var mUnitBounds = RectF()
    protected var mCircleOuterContour = RectF()
    protected var mCircleInnerContour = RectF()
    //Other
    // The text to show
    private var mText: String? = ""
    private var mTextLength: Int = 0
    /**
     * @param _unit The unit to show next to the current value.
     * You also need to set [.setUnitVisible] to true.
     */
    var unit: String? = ""
        set(_unit) {
            if (_unit == null) {
                field = ""
            } else {
                field = _unit
            }
            invalidate()
        }
    private var mUnitPosition = UnitPosition.RIGHT_TOP
    /**
     * Indicates if the given text, the current percentage, or the current value should be shown.
     */
    private var mTextMode = TextMode.PERCENT
    /**
     * @return true if auto text size is enabled, false otherwise.
     */
    /**
     * @param _autoTextSize true to enable auto text size calculation.
     */
    var isAutoTextSize: Boolean = false
    /**
     * @param _showUnit True to show unit, false to hide it.
     */
    // triggers recalculating text sizes
    var isUnitVisible = false
        set(_showUnit) {
            if (_showUnit != isUnitVisible) {
                field = _showUnit
                triggerReCalcTextSizesAndPositions()
            }
        }
    //clipping
    private var mClippingBitmap: Bitmap? = null
    private val mMaskPaint: Paint
    /**
     * Relative size of the unite string to the value string.
     */
    /**
     * @return The relative size (scale factor) of the unit text size to the text size
     */
    var relativeUniteSize = 1f
        private set
    var isSeekModeEnabled = false
    /**
     * @param shouldDrawTextWhileSpinning True to show text in spinning mode, false to hide it.
     */
    var isShowTextWhileSpinning = false
    var isShowBlock = false
    var blockCount = 18
        set(blockCount) {
            if (blockCount > 1) {
                isShowBlock = true
                field = blockCount
                mBlockDegree = 360.0f / blockCount
                mBlockScaleDegree = mBlockDegree * blockScale
            } else {
                isShowBlock = false
            }
        }
    var blockScale = 0.9f
        set(@FloatRange(from = 0.0, to = 1.0) blockScale) {
            if (blockScale >= 0.0f && blockScale <= 1.0f) {
                field = blockScale
                mBlockScaleDegree = mBlockDegree * blockScale
            }
        }
    private var mBlockDegree = (360 / blockCount).toFloat()
    private var mBlockScaleDegree = mBlockDegree * blockScale


    private var mTouchEventCount: Int = 0
    private var onProgressChangedListener: OnProgressChangedListener? = null
    private var previousProgressChangedValue: Float = 0.toFloat()


    private var decimalFormat = DecimalFormat("0")

    // Text typeface
    private var textTypeface: Typeface? = null
    private var unitTextTypeface: Typeface? = null


    val fillColor: Int
        get() {
            return mBackgroundCirclePaint.getColor()
        }

    var rimShader: Shader
        get() {
            return mRimPaint.getShader()
        }
        set(shader) {
            this.mRimPaint.setShader(shader)
        }

    fun calcTextColor(): Int {
        return mTextColor
    }

    /**
     * Sets the text color.
     * You also need to  set [.setTextColorAuto] to false to see your color.
     *
     * @param textColor the color
     */
    fun setTextColor(@ColorInt textColor: Int) {
        mTextColor = textColor
        mTextPaint.setColor(textColor)
    }

    /**
     * Sets the color of progress bar.
     *
     * @param barColors One or more colors. If more than one color is specified, a gradient of the colors is used.
     */
    fun setBarColor(@ColorInt vararg barColors: Int) {
        this.barColors = barColors
        if (barColors.size > 1) {
            mBarPaint.setShader(SweepGradient(mCircleBounds.centerX(), mCircleBounds.centerY(), barColors, null))
            val matrix = Matrix()
            mBarPaint.getShader().getLocalMatrix(matrix)

            matrix.postTranslate(-mCircleBounds.centerX(), -mCircleBounds.centerY())
            matrix.postRotate(startAngle.toFloat())
            matrix.postTranslate(mCircleBounds.centerX(), mCircleBounds.centerY())
            mBarPaint.getShader().setLocalMatrix(matrix)
            mBarPaint.setColor(barColors[0])
        } else if (barColors.size == 1) {
            mBarPaint.setColor(barColors[0])
            mBarPaint.setShader(null)
        } else {
            mBarPaint.setColor(mBarColorStandard)
            mBarPaint.setShader(null)
        }
    }

    /**
     * @param _clippingBitmap The bitmap used for clipping. Set to null to disable clipping.
     * Default: No clipping.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    fun setClippingBitmap(_clippingBitmap: Bitmap) {

        if (getWidth() > 0 && getHeight() > 0) {
            mClippingBitmap = Bitmap.createScaledBitmap(_clippingBitmap, getWidth(), getHeight(), false)
        } else {
            mClippingBitmap = _clippingBitmap
        }
        if (mClippingBitmap == null) {
            // enable HW acceleration
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            // disable HW acceleration
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }

    /**
     * Sets the background color of the entire Progress Circle.
     * Set the color to 0x00000000 (Color.TRANSPARENT) to hide it.
     *
     * @param circleColor the color.
     */
    fun setFillCircleColor(@ColorInt circleColor: Int) {
        mBackgroundCircleColor = circleColor
        mBackgroundCirclePaint.setColor(circleColor)
    }

    fun setOnAnimationStateChangedListener(_animationStateChangedListener: AnimationStateChangedListener) {
        mAnimationStateChangedListener = _animationStateChangedListener
    }

    fun setOnProgressChangedListener(listener: OnProgressChangedListener) {
        onProgressChangedListener = listener
    }

    /**
     * @param _color The color of progress the bar in spinning mode.
     */
    fun setSpinBarColor(@ColorInt _color: Int) {
        mSpinnerColor = _color
        mBarSpinnerPaint.setColor(mSpinnerColor)
    }

    /**
     * Length of spinning bar in degree.
     *
     * @param barLength length in degree
     */
    fun setSpinningBarLength(@FloatRange(from = 0.0) barLength: Float) {
        mSpinningBarLengthOrig = barLength
        this.mSpinningBarLengthCurrent = mSpinningBarLengthOrig
    }

    /**
     * Set the text in the middle of the circle view.
     * You need also set the [TextMode] to TextMode.TEXT to see the text.
     *
     * @param text The text to show
     */
    fun setText(text: String?) {
        mText = if (text != null) text else ""
        invalidate()
    }

    /**
     * If auto text color is enabled, the text color  and the unit color is always the same as the rim color.
     * This is useful if the rim has multiple colors (color gradient), than the text will always have
     * the color of the tip of the rim.
     *
     * @param isEnabled true to enable, false to disable
     */
    fun setTextColorAuto(isEnabled: Boolean) {
        mIsAutoColorEnabled = isEnabled
    }

    /**
     * Sets the auto text mode.
     *
     * @param _textValue The mode
     */
    fun setTextMode(_textValue: TextMode) {
        mTextMode = _textValue
    }

    /**
     * @param typeface The typeface to use for the text
     */
    fun setTextTypeface(typeface: Typeface) {
        mTextPaint.setTypeface(typeface)
    }

    /**
     * Sets the unit text color.
     * Also sets [.setTextColorAuto] to false
     *
     * @param unitColor The color.
     */
    fun setUnitColor(@ColorInt unitColor: Int) {
        mUnitColor = unitColor
        mUnitTextPaint.setColor(unitColor)
        mIsAutoColorEnabled = false
    }

    fun setUnitPosition(_unitPosition: UnitPosition) {
        mUnitPosition = _unitPosition
        triggerReCalcTextSizesAndPositions() // triggers recalculating text sizes
    }

    /**
     * @param typeface The typeface to use for the unit text
     */
    fun setUnitTextTypeface(typeface: Typeface) {
        mUnitTextPaint.setTypeface(typeface)
    }

    /**
     * @param _relativeUniteSize The relative scale factor of the unit text size to the text size.
     * Only useful for autotextsize=true; Effects both, the unit text size and the text size.
     */
    fun setUnitToTextScale(@FloatRange(from = 0.0) _relativeUniteSize: Float) {
        relativeUniteSize = _relativeUniteSize
        triggerReCalcTextSizesAndPositions()
    }

    /**
     * Set the value of the circle view without an animation.
     * Stops any currently active animations.
     *
     * @param _value The value.
     */
    fun setValue(_value: Float) {
        val msg = Message()
        msg.what = AnimationMsg.SET_VALUE.ordinal
        msg.obj = floatArrayOf(_value, _value)
        mAnimationHandler.sendMessage(msg)
        triggerOnProgressChanged(_value)
    }

    /**
     * Sets the value of the circle view with an animation.
     * The current value is used as the start value of the animation
     *
     * @param _valueTo value after animation
     */
    fun setValueAnimated(_valueTo: Float) {
        setValueAnimated(_valueTo, 1200)
    }

    /**
     * Sets the value of the circle view with an animation.
     * The current value is used as the start value of the animation
     *
     * @param _valueTo           value after animation
     * @param _animationDuration the duration of the animation in milliseconds.
     */
    fun setValueAnimated(_valueTo: Float, _animationDuration: Long) {
        setValueAnimated(mCurrentValue, _valueTo, _animationDuration)
    }

    /**
     * Sets the value of the circle view with an animation.
     *
     * @param _valueFrom         start value of the animation
     * @param _valueTo           value after animation
     * @param _animationDuration the duration of the animation in milliseconds
     */
    fun setValueAnimated(_valueFrom: Float, _valueTo: Float, _animationDuration: Long) {
        mAnimationDuration = _animationDuration.toDouble()
        val msg = Message()
        msg.what = AnimationMsg.SET_VALUE_ANIMATED.ordinal
        msg.obj = floatArrayOf(_valueFrom, _valueTo)
        mAnimationHandler.sendMessage(msg)
        triggerOnProgressChanged(_valueTo)
    }

    fun getDecimalFormat(): DecimalFormat {
        return decimalFormat
    }

    fun setDecimalFormat(decimalFormat: DecimalFormat?) {
        if (decimalFormat == null) {
            throw IllegalArgumentException("decimalFormat must not be null!")
        }
        this.decimalFormat = decimalFormat
    }

    /**
     * Sets interpolator for value animations.
     *
     * @param interpolator the interpolator
     */
    fun setValueInterpolator(interpolator: TimeInterpolator) {
        mAnimationHandler.setValueInterpolator(interpolator)
    }


    /**
     * Sets the interpolator for length changes of the bar.
     *
     * @param interpolator the interpolator
     */
    fun setLengthChangeInterpolator(interpolator: TimeInterpolator) {
        mAnimationHandler.setLengthChangeInterpolator(interpolator)
    }

    init {

        parseAttributes(context.obtainStyledAttributes(attrs,
                R.styleable.CircleProgressView))

        if (!isInEditMode()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
            }
        }

        mMaskPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mMaskPaint.setFilterBitmap(false)
        mMaskPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.DST_IN))
        setupPaints()
    }

    /**
     * Parse the attributes passed to the view from the XML
     *
     * @param a the attributes to parse
     */
    private fun parseAttributes(a: TypedArray) {
        barWidth = a.getDimension(R.styleable.CircleProgressView_cpv_barWidth,
                barWidth.toFloat()).toInt()

        rimWidth = a.getDimension(R.styleable.CircleProgressView_cpv_rimWidth,
                rimWidth.toFloat()).toInt()

        spinSpeed = a.getFloat(R.styleable.CircleProgressView_cpv_spinSpeed,
                spinSpeed)

        val value = a.getFloat(R.styleable.CircleProgressView_cpv_value, mCurrentValue)
        setValue(value)
        mCurrentValue = value

        if (a.hasValue(R.styleable.CircleProgressView_cpv_barColor) && a.hasValue(R.styleable.CircleProgressView_cpv_barColor1) && a.hasValue(R.styleable.CircleProgressView_cpv_barColor2) && a.hasValue(R.styleable.CircleProgressView_cpv_barColor3)) {
            barColors = intArrayOf(a.getColor(R.styleable.CircleProgressView_cpv_barColor, mBarColorStandard), a.getColor(R.styleable.CircleProgressView_cpv_barColor1, mBarColorStandard), a.getColor(R.styleable.CircleProgressView_cpv_barColor2, mBarColorStandard), a.getColor(R.styleable.CircleProgressView_cpv_barColor3, mBarColorStandard))

        } else if (a.hasValue(R.styleable.CircleProgressView_cpv_barColor) && a.hasValue(R.styleable.CircleProgressView_cpv_barColor1) && a.hasValue(R.styleable.CircleProgressView_cpv_barColor2)) {

            barColors = intArrayOf(a.getColor(R.styleable.CircleProgressView_cpv_barColor, mBarColorStandard), a.getColor(R.styleable.CircleProgressView_cpv_barColor1, mBarColorStandard), a.getColor(R.styleable.CircleProgressView_cpv_barColor2, mBarColorStandard))

        } else if (a.hasValue(R.styleable.CircleProgressView_cpv_barColor) && a.hasValue(R.styleable.CircleProgressView_cpv_barColor1)) {

            barColors = intArrayOf(a.getColor(R.styleable.CircleProgressView_cpv_barColor, mBarColorStandard), a.getColor(R.styleable.CircleProgressView_cpv_barColor1, mBarColorStandard))

        } else {
            barColors = intArrayOf(a.getColor(R.styleable.CircleProgressView_cpv_barColor, mBarColorStandard), a.getColor(R.styleable.CircleProgressView_cpv_barColor, mBarColorStandard))
        }

        setSpinBarColor(a.getColor(R.styleable.CircleProgressView_cpv_spinColor, mSpinnerColor))


        setSpinningBarLength(a.getFloat(R.styleable.CircleProgressView_cpv_spinBarLength,
                mSpinningBarLengthOrig))


        if (a.hasValue(R.styleable.CircleProgressView_cpv_textSize)) {
            textSize = a.getDimension(R.styleable.CircleProgressView_cpv_textSize, textSize.toFloat()).toInt()
        }
        if (a.hasValue(R.styleable.CircleProgressView_cpv_unitSize)) {
            unitSize = a.getDimension(R.styleable.CircleProgressView_cpv_unitSize, unitSize.toFloat()).toInt()
        }
        if (a.hasValue(R.styleable.CircleProgressView_cpv_textColor)) {
            setTextColor(a.getColor(R.styleable.CircleProgressView_cpv_textColor, mTextColor))
        }
        if (a.hasValue(R.styleable.CircleProgressView_cpv_unitColor)) {
            setUnitColor(a.getColor(R.styleable.CircleProgressView_cpv_unitColor, mUnitColor))
        }
        if (a.hasValue(R.styleable.CircleProgressView_cpv_autoTextColor)) {
            setTextColorAuto(a.getBoolean(R.styleable.CircleProgressView_cpv_autoTextColor, mIsAutoColorEnabled))
        }
        if (a.hasValue(R.styleable.CircleProgressView_cpv_autoTextSize)) {
            isAutoTextSize = a.getBoolean(R.styleable.CircleProgressView_cpv_autoTextSize, isAutoTextSize)
        }
        if (a.hasValue(R.styleable.CircleProgressView_cpv_textMode)) {
            setTextMode(TextMode.values()[a.getInt(R.styleable.CircleProgressView_cpv_textMode, 0)])
        }
        if (a.hasValue(R.styleable.CircleProgressView_cpv_unitPosition)) {
            setUnitPosition(UnitPosition.values()[a.getInt(R.styleable.CircleProgressView_cpv_unitPosition, 3)])
        }
        //if the mText is empty, show current percentage value
        if (a.hasValue(R.styleable.CircleProgressView_cpv_text)) {
            setText(a.getString(R.styleable.CircleProgressView_cpv_text))
        }

        setUnitToTextScale(a.getFloat(R.styleable.CircleProgressView_cpv_unitToTextScale, 1f))

        rimColor = a.getColor(R.styleable.CircleProgressView_cpv_rimColor,
                rimColor)

        setFillCircleColor(a.getColor(R.styleable.CircleProgressView_cpv_fillColor,
                mBackgroundCircleColor))

        contourColor = a.getColor(R.styleable.CircleProgressView_cpv_contourColor, contourColor)
        contourSize = a.getDimension(R.styleable.CircleProgressView_cpv_contourSize, contourSize)

        maxValue = a.getFloat(R.styleable.CircleProgressView_cpv_maxValue, maxValue)

        unit = a.getString(R.styleable.CircleProgressView_cpv_unit)
        isUnitVisible = a.getBoolean(R.styleable.CircleProgressView_cpv_showUnit, isUnitVisible)

        textScale = a.getFloat(R.styleable.CircleProgressView_cpv_textScale, textScale)
        unitScale = a.getFloat(R.styleable.CircleProgressView_cpv_unitScale, unitScale)

        isSeekModeEnabled = a.getBoolean(R.styleable.CircleProgressView_cpv_seekMode, isSeekModeEnabled)

        startAngle = a.getInt(R.styleable.CircleProgressView_cpv_startAngle, startAngle)

        isShowTextWhileSpinning = a.getBoolean(R.styleable.CircleProgressView_cpv_showTextInSpinningMode, isShowTextWhileSpinning)

        if (a.hasValue(R.styleable.CircleProgressView_cpv_blockCount)) {
            blockCount = a.getInt(R.styleable.CircleProgressView_cpv_blockCount, 1)
            blockScale = a.getFloat(R.styleable.CircleProgressView_cpv_blockScale, 0.9f)
        }


        if (a.hasValue(R.styleable.CircleProgressView_cpv_textTypeface)) {
            try {
                textTypeface = Typeface.createFromAsset(getContext().getAssets(), a.getString(R.styleable.CircleProgressView_cpv_textTypeface))
            } catch (exception: Exception) {
                // error while trying to inflate typeface (is the path set correctly?)
            }

        }
        if (a.hasValue(R.styleable.CircleProgressView_cpv_unitTypeface)) {
            try {
                unitTextTypeface = Typeface.createFromAsset(getContext().getAssets(), a.getString(R.styleable.CircleProgressView_cpv_unitTypeface))
            } catch (exception: Exception) {
                // error while trying to inflate typeface (is the path set correctly?)
            }

        }

        if (a.hasValue(R.styleable.CircleProgressView_cpv_decimalFormat)) {
            try {
                val pattern = a.getString(R.styleable.CircleProgressView_cpv_decimalFormat)
                if (pattern != null) {
                    decimalFormat = DecimalFormat(pattern)
                }

            } catch (exception: Exception) {
                Log.w(TAG, exception.message)
            }

        }


        // Recycle
        a.recycle()
    }

    /*
        * When this is called, make the view square.
        * From: http://www.jayway.com/2012/12/12/creating-custom-android-views-part-4-measuring-and-how-to-force-a-view-to-be-square/
        *
        */
    protected override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // The first thing that happen is that we call the superclass
        // implementation of onMeasure. The reason for that is that measuring
        // can be quite a complex process and calling the super method is a
        // convenient way to get most of this complexity handled.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // We can�t use getWidth() or getHeight() here. During the measuring
        // pass the view has not gotten its final size yet (this happens first
        // at the start of the layout pass) so we have to use getMeasuredWidth()
        // and getMeasuredHeight().
        val size: Int
        val width = getMeasuredWidth()
        val height = getMeasuredHeight()
        val widthWithoutPadding = width - getPaddingLeft() - getPaddingRight()
        val heightWithoutPadding = height - getPaddingTop() - getPaddingBottom()


        // Finally we have some simple logic that calculates the size of the view
        // and calls setMeasuredDimension() to set that size.
        // Before we compare the width and height of the view, we remove the padding,
        // and when we set the dimension we add it back again. Now the actual content
        // of the view will be square, but, depending on the padding, the total dimensions
        // of the view might not be.
        if (widthWithoutPadding > heightWithoutPadding) {
            size = heightWithoutPadding
        } else {
            size = widthWithoutPadding
        }

        // If you override onMeasure() you have to call setMeasuredDimension().
        // This is how you report back the measured size.  If you don�t call
        // setMeasuredDimension() the parent will throw an exception and your
        // application will crash.
        // We are calling the onMeasure() method of the superclass so we don�t
        // actually need to call setMeasuredDimension() since that takes care
        // of that. However, the purpose with overriding onMeasure() was to
        // change the default behaviour and to do that we need to call
        // setMeasuredDimension() with our own values.
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom())
    }

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of the view,
     * because this method is called after measuring the dimensions of MATCH_PARENT and WRAP_CONTENT.
     * Use this dimensions to setup the bounds and paints.
     */
    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Share the dimensions
        mLayoutWidth = w
        mLayoutHeight = h

        setupBounds()
        setupBarPaint()

        if (mClippingBitmap != null) {
            mClippingBitmap = Bitmap.createScaledBitmap(mClippingBitmap!!, getWidth(), getHeight(), false)
        }

        invalidate()
    }

    //----------------------------------
    // region helper
    private fun calcTextSizeForCircle(_text: String, _textPaint: Paint, _circleBounds: RectF): Float {

        //get mActualTextBounds bounds
        val innerCircleBounds = getInnerCircleRect(_circleBounds)
        return calcTextSizeForRect(_text, _textPaint, innerCircleBounds)

    }

    private fun getInnerCircleRect(_circleBounds: RectF): RectF {

        val circleWidth = (+_circleBounds.width() - (Math.max(barWidth, rimWidth)).toFloat() - (contourSize * 2)).toDouble()
        val width = ((circleWidth / 2.0) * Math.sqrt(2.0))
        val widthDelta = (_circleBounds.width() - width.toFloat()) / 2f

        var scaleX = 1f
        var scaleY = 1f
        if (isUnitVisible) {
            when (mUnitPosition) {
                UnitPosition.TOP, UnitPosition.BOTTOM -> {
                    scaleX = 1.1f // scaleX square to rectangle, so the longer text with unit fits better
                    scaleY = 0.88f
                }
                UnitPosition.LEFT_TOP, UnitPosition.RIGHT_TOP, UnitPosition.LEFT_BOTTOM, UnitPosition.RIGHT_BOTTOM -> {
                    scaleX = 0.77f // scaleX square to rectangle, so the longer text with unit fits better
                    scaleY = 1.33f
                }
            }

        }
        return RectF(_circleBounds.left + (widthDelta * scaleX), _circleBounds.top + (widthDelta * scaleY), _circleBounds.right - (widthDelta * scaleX), _circleBounds.bottom - (widthDelta * scaleY))

    }


    private fun triggerOnProgressChanged(value: Float) {
        if (onProgressChangedListener != null && value != previousProgressChangedValue) {
            onProgressChangedListener!!.onProgressChanged(value)
            previousProgressChangedValue = value
        }
    }

    private fun triggerReCalcTextSizesAndPositions() {
        mTextLength = -1
        mOuterTextBounds = getInnerCircleRect(mCircleBounds)
        invalidate()
    }

    private fun calcTextColor(value: Double): Int {
        if (barColors.size > 1) {
            val percent = 1f / maxValue * value
            var low = Math.floor((barColors.size - 1) * percent).toInt()
            var high = low + 1
            if (low < 0) {
                low = 0
                high = 1
            } else if (high >= barColors.size) {
                low = barColors.size - 2
                high = barColors.size - 1
            }
            return ColorUtils.getRGBGradient(barColors[low], barColors[high], (1 - (barColors.size - 1) * percent % 1.0).toFloat());
        } else if (barColors.size == 1) {
            return barColors[0]
        } else {
            return Color.BLACK
        }
    }

    private fun setTextSizeAndTextBoundsWithAutoTextSize(unitGapWidthHalf: Float, unitWidth: Float, unitGapHeightHalf: Float, unitHeight: Float, text: String?) {
        var textRect = mOuterTextBounds

        if (isUnitVisible) {

            //shrink text Rect so that there is space for the unit
            when (mUnitPosition) {

                UnitPosition.TOP -> textRect = RectF(mOuterTextBounds.left, mOuterTextBounds.top + unitHeight + unitGapHeightHalf, mOuterTextBounds.right, mOuterTextBounds.bottom)
                UnitPosition.BOTTOM -> textRect = RectF(mOuterTextBounds.left, mOuterTextBounds.top, mOuterTextBounds.right, mOuterTextBounds.bottom - unitHeight - unitGapHeightHalf)
                UnitPosition.LEFT_TOP, UnitPosition.LEFT_BOTTOM -> textRect = RectF(mOuterTextBounds.left + unitWidth + unitGapWidthHalf, mOuterTextBounds.top, mOuterTextBounds.right, mOuterTextBounds.bottom)
                UnitPosition.RIGHT_TOP, UnitPosition.RIGHT_BOTTOM -> textRect = RectF(mOuterTextBounds.left, mOuterTextBounds.top, mOuterTextBounds.right - unitWidth - unitGapWidthHalf, mOuterTextBounds.bottom)
                else -> textRect = RectF(mOuterTextBounds.left, mOuterTextBounds.top, mOuterTextBounds.right - unitWidth - unitGapWidthHalf, mOuterTextBounds.bottom)
            }

        }

        mTextPaint.setTextSize(calcTextSizeForRect(text!!, mTextPaint, textRect) * textScale)
        mActualTextBounds = calcTextBounds(text, mTextPaint, textRect) // center text in text rect
    }

    private fun setTextSizeAndTextBoundsWithFixedTextSize(text: String?) {
        mTextPaint.setTextSize(textSize.toFloat())
        mActualTextBounds = calcTextBounds(text, mTextPaint, mCircleBounds) //center text in circle
    }

    private fun setUnitTextBoundsAndSizeWithAutoTextSize(unitGapWidthHalf: Float, unitWidth: Float, unitGapHeightHalf: Float, unitHeight: Float) {
        //calc the rectangle containing the unit text
        when (mUnitPosition) {

            UnitPosition.TOP -> {
                mUnitBounds = RectF(mOuterTextBounds.left, mOuterTextBounds.top, mOuterTextBounds.right, mOuterTextBounds.top + unitHeight - unitGapHeightHalf)
            }
            UnitPosition.BOTTOM -> mUnitBounds = RectF(mOuterTextBounds.left, mOuterTextBounds.bottom - unitHeight + unitGapHeightHalf, mOuterTextBounds.right, mOuterTextBounds.bottom)
            UnitPosition.LEFT_TOP, UnitPosition.LEFT_BOTTOM -> {
                mUnitBounds = RectF(mOuterTextBounds.left, mOuterTextBounds.top, mOuterTextBounds.left + unitWidth - unitGapWidthHalf, mOuterTextBounds.top + unitHeight)
            }
            UnitPosition.RIGHT_TOP, UnitPosition.RIGHT_BOTTOM -> {
                mUnitBounds = RectF(mOuterTextBounds.right - unitWidth + unitGapWidthHalf, mOuterTextBounds.top, mOuterTextBounds.right, mOuterTextBounds.top + unitHeight)
            }
            else -> {
                mUnitBounds = RectF(mOuterTextBounds.right - unitWidth + unitGapWidthHalf, mOuterTextBounds.top, mOuterTextBounds.right, mOuterTextBounds.top + unitHeight)
            }
        }

        mUnitTextPaint.setTextSize(calcTextSizeForRect(unit!!, mUnitTextPaint, mUnitBounds) * unitScale)
        mUnitBounds = calcTextBounds(unit, mUnitTextPaint, mUnitBounds) // center text in rectangle and reuse it

        when (mUnitPosition) {


            UnitPosition.LEFT_TOP, UnitPosition.RIGHT_TOP -> {
                //move unite to top of text
                val dy = mActualTextBounds.top - mUnitBounds.top
                mUnitBounds.offset(0f, dy)
            }
            UnitPosition.LEFT_BOTTOM, UnitPosition.RIGHT_BOTTOM -> {
                //move unite to bottom of text
                val dy = mActualTextBounds.bottom - mUnitBounds.bottom
                mUnitBounds.offset(0f, dy)
            }
        }
    }

    private fun setUnitTextBoundsAndSizeWithFixedTextSize(unitGapWidth: Float, unitGapHeight: Float) {
        mUnitTextPaint.setTextSize(unitSize.toFloat())
        mUnitBounds = calcTextBounds(unit, mUnitTextPaint, mOuterTextBounds) // center text in rectangle and reuse it

        when (mUnitPosition) {

            UnitPosition.TOP -> mUnitBounds.offsetTo(mUnitBounds.left, mActualTextBounds.top - unitGapHeight - mUnitBounds.height())
            UnitPosition.BOTTOM -> mUnitBounds.offsetTo(mUnitBounds.left, mActualTextBounds.bottom + unitGapHeight)
            UnitPosition.LEFT_TOP, UnitPosition.LEFT_BOTTOM -> mUnitBounds.offsetTo(mActualTextBounds.left - unitGapWidth - mUnitBounds.width(), mUnitBounds.top)
            UnitPosition.RIGHT_TOP, UnitPosition.RIGHT_BOTTOM -> mUnitBounds.offsetTo(mActualTextBounds.right + unitGapWidth, mUnitBounds.top)
            else -> mUnitBounds.offsetTo(mActualTextBounds.right + unitGapWidth, mUnitBounds.top)
        }

        when (mUnitPosition) {
            UnitPosition.LEFT_TOP, UnitPosition.RIGHT_TOP -> {
                //move unite to top of text
                val dy = mActualTextBounds.top - mUnitBounds.top
                mUnitBounds.offset(0f, dy)
            }
            UnitPosition.LEFT_BOTTOM, UnitPosition.RIGHT_BOTTOM -> {
                //move unite to bottom of text
                val dy = mActualTextBounds.bottom - mUnitBounds.bottom
                mUnitBounds.offset(0f, dy)
            }
        }
    }


    /**
     * Returns the bounding rectangle of the given _text, with the size and style defined in the _textPaint centered in the middle of the _textBounds
     *
     * @param _text       The text.
     * @param _textPaint  The paint defining the text size and style.
     * @param _textBounds The rect where the text will be centered.
     * @return The bounding box of the text centered in the _textBounds.
     */
    private fun calcTextBounds(_text: String?, _textPaint: Paint, _textBounds: RectF): RectF {

        val textBoundsTmp = Rect()

        //get current text bounds
        _textPaint.getTextBounds(_text, 0, _text!!.length, textBoundsTmp)
        val width = (textBoundsTmp.left + textBoundsTmp.width()).toFloat()
        val height = textBoundsTmp.bottom + textBoundsTmp.height() * 0.93f // the height of calcTextBounds is a bit to high, therefore  * 0.93
        //center in circle
        val textRect = RectF()
        textRect.left = (_textBounds.left + ((_textBounds.width() - width) / 2))
        textRect.top = _textBounds.top + ((_textBounds.height() - height) / 2)
        textRect.right = textRect.left + width
        textRect.bottom = textRect.top + height


        return textRect
    }

    //endregion helper
    //----------------------------------

    //----------------------------------
    //region Setting up stuff

    /**
     * Set the bounds of the component
     */
    private fun setupBounds() {
        // Width should equal to Height, find the min value to setup the circle
        val minValue = Math.min(mLayoutWidth, mLayoutHeight)

        // Calc the Offset if needed
        val xOffset = mLayoutWidth - minValue
        val yOffset = mLayoutHeight - minValue

        // Add the offset
        val paddingTop = (this.getPaddingTop() + (yOffset / 2)).toFloat()
        val paddingBottom = (this.getPaddingBottom() + (yOffset / 2)).toFloat()
        val paddingLeft = (this.getPaddingLeft() + (xOffset / 2)).toFloat()
        val paddingRight = (this.getPaddingRight() + (xOffset / 2)).toFloat()

        val width = getWidth() //this.getLayoutParams().width;
        val height = getHeight() //this.getLayoutParams().height;


        val circleWidthHalf = if (barWidth / 2f > rimWidth / 2f + contourSize) barWidth / 2f else rimWidth / 2f + contourSize

        mCircleBounds = RectF(paddingLeft + circleWidthHalf,
                paddingTop + circleWidthHalf,
                width.toFloat() - paddingRight - circleWidthHalf,
                height.toFloat() - paddingBottom - circleWidthHalf)


        mInnerCircleBound = RectF(paddingLeft + (barWidth),
                paddingTop + (barWidth),
                width.toFloat() - paddingRight - (barWidth).toFloat(),
                height.toFloat() - paddingBottom - (barWidth).toFloat())
        mOuterTextBounds = getInnerCircleRect(mCircleBounds)
        mCircleInnerContour = RectF(mCircleBounds.left + (rimWidth / 2.0f) + (contourSize / 2.0f), mCircleBounds.top + (rimWidth / 2.0f) + (contourSize / 2.0f), mCircleBounds.right - (rimWidth / 2.0f) - (contourSize / 2.0f), mCircleBounds.bottom - (rimWidth / 2.0f) - (contourSize / 2.0f))
        mCircleOuterContour = RectF(mCircleBounds.left - (rimWidth / 2.0f) - (contourSize / 2.0f), mCircleBounds.top - (rimWidth / 2.0f) - (contourSize / 2.0f), mCircleBounds.right + (rimWidth / 2.0f) + (contourSize / 2.0f), mCircleBounds.bottom + (rimWidth / 2.0f) + (contourSize / 2.0f))

        mCenter = PointF(mCircleBounds.centerX(), mCircleBounds.centerY())
    }

    private fun setupBarPaint() {

        if (barColors.size > 1) {
            mBarPaint.setShader(SweepGradient(mCircleBounds.centerX(), mCircleBounds.centerY(), barColors, null))
            val matrix = Matrix()
            mBarPaint.getShader().getLocalMatrix(matrix)

            matrix.postTranslate(-mCircleBounds.centerX(), -mCircleBounds.centerY())
            matrix.postRotate(startAngle.toFloat())
            matrix.postTranslate(mCircleBounds.centerX(), mCircleBounds.centerY())
            mBarPaint.getShader().setLocalMatrix(matrix)
        } else {
            mBarPaint.setColor(barColors[0])
            mBarPaint.setShader(null)
        }

        mBarPaint.setAntiAlias(true)
        mBarPaint.setStrokeCap(barStrokeCap)
        mBarPaint.setStyle(Style.STROKE)
        mBarPaint.setStrokeWidth(barWidth.toFloat())
    }


    /**
     * Setup all paints.
     * Call only if changes to color or size properties are not visible.
     */
    fun setupPaints() {
        setupBarPaint()
        setupBarSpinnerPaint()
        setupContourPaint()
        setupUnitTextPaint()
        setupTextPaint()
        setupBackgroundCirclePaint()
        setupRimPaint()
    }

    private fun setupContourPaint() {
        mContourPaint.setColor(contourColor)
        mContourPaint.setAntiAlias(true)
        mContourPaint.setStyle(Style.STROKE)
        mContourPaint.setStrokeWidth(contourSize)
    }

    private fun setupUnitTextPaint() {
        mUnitTextPaint.setStyle(Style.FILL)
        mUnitTextPaint.setAntiAlias(true)
        if (unitTextTypeface != null) {
            mUnitTextPaint.setTypeface(unitTextTypeface)
        }
    }

    private fun setupTextPaint() {
        mTextPaint.setSubpixelText(true)
        mTextPaint.setLinearText(true)
        mTextPaint.setTypeface(Typeface.MONOSPACE)
        mTextPaint.setColor(mTextColor)
        mTextPaint.setStyle(Style.FILL)
        mTextPaint.setAntiAlias(true)
        mTextPaint.setTextSize(textSize.toFloat())
        if (textTypeface != null) {
            mTextPaint.setTypeface(textTypeface)
        } else {
            mTextPaint.setTypeface(Typeface.MONOSPACE)
        }

    }


    private fun setupBackgroundCirclePaint() {
        mBackgroundCirclePaint.setColor(mBackgroundCircleColor)
        mBackgroundCirclePaint.setAntiAlias(true)
        mBackgroundCirclePaint.setStyle(Style.FILL)
    }

    private fun setupRimPaint() {
        mRimPaint.setColor(rimColor)
        mRimPaint.setAntiAlias(true)
        mRimPaint.setStyle(Style.STROKE)
        mRimPaint.setStrokeWidth(rimWidth.toFloat())
    }

    private fun setupBarSpinnerPaint() {
        mBarSpinnerPaint.setAntiAlias(true)
        mBarSpinnerPaint.setStrokeCap(spinnerStrokeCap)
        mBarSpinnerPaint.setStyle(Style.STROKE)
        mBarSpinnerPaint.setStrokeWidth(barWidth.toFloat())
        mBarSpinnerPaint.setColor(mSpinnerColor)
    }

    //endregion Setting up stuff
    //----------------------------------

    //----------------------------------
    //region draw all the things

    protected override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (DEBUG) {
            drawDebug(canvas)
        }

        val degrees = (360f / maxValue * mCurrentValue)

        // Draw the background circle
        if (mBackgroundCircleColor != 0) {
            canvas.drawArc(mInnerCircleBound, 360f, 360f, false, mBackgroundCirclePaint)
        }
        //Draw the rim
        if (rimWidth > 0) {
            if (!isShowBlock) {
                canvas.drawArc(mCircleBounds, 360f, 360f, false, mRimPaint)
            } else {
                drawBlocks(canvas, mCircleBounds, startAngle.toFloat(), 360f, false, mRimPaint)
            }
        }
        //Draw contour
        if (contourSize > 0) {
            canvas.drawArc(mCircleOuterContour, 360f, 360f, false, mContourPaint)
            canvas.drawArc(mCircleInnerContour, 360f, 360f, false, mContourPaint)
        }


        //Draw spinner
        if (mAnimationState == AnimationState.SPINNING || mAnimationState == AnimationState.END_SPINNING) {
            drawSpinner(canvas)
            if (isShowTextWhileSpinning) {
                drawTextWithUnit(canvas)
            }

        } else if (mAnimationState == AnimationState.END_SPINNING_START_ANIMATING) {
            //draw spinning arc
            drawSpinner(canvas)

            if (mDrawBarWhileSpinning) {
                drawBar(canvas, degrees)
                drawTextWithUnit(canvas)
            } else if (isShowTextWhileSpinning) {
                drawTextWithUnit(canvas)
            }

        } else {
            drawBar(canvas, degrees)
            drawTextWithUnit(canvas)
        }


        if (mClippingBitmap != null) {
            canvas.drawBitmap(mClippingBitmap!!, 0f, 0f, mMaskPaint)
        }

    }

    private fun drawDebug(canvas: Canvas) {
        val innerRectPaint = Paint()
        innerRectPaint.setColor(Color.YELLOW)
        canvas.drawRect(mCircleBounds, innerRectPaint)

    }

    private fun drawBlocks(_canvas: Canvas, circleBounds: RectF, startAngle: Float, _degrees: Float, userCenter: Boolean, paint: Paint) {
        var tmpDegree = 0.0f
        while (tmpDegree < _degrees) {
            _canvas.drawArc(circleBounds, startAngle + tmpDegree, Math.min(mBlockScaleDegree, _degrees - tmpDegree), userCenter, paint)
            tmpDegree += mBlockDegree
        }
    }

    private fun drawSpinner(canvas: Canvas) {

        if (mSpinningBarLengthCurrent < 0) {
            mSpinningBarLengthCurrent = 1f
        }
        val startAngle = (this.startAngle + mCurrentSpinnerDegreeValue - mSpinningBarLengthCurrent)
        canvas.drawArc(mCircleBounds, startAngle, mSpinningBarLengthCurrent, false,
                mBarSpinnerPaint)

    }

    private fun drawTextWithUnit(canvas: Canvas) {

        val relativeGapHeight: Float
        val relativeGapWidth: Float
        val relativeHeight: Float
        val relativeWidth: Float

        when (mUnitPosition) {
            UnitPosition.TOP, UnitPosition.BOTTOM -> {
                relativeGapWidth = 0.05f //gap size between text and unit
                relativeGapHeight = 0.025f //gap size between text and unit
                relativeHeight = 0.25f * relativeUniteSize
                relativeWidth = 0.4f * relativeUniteSize
            }
            UnitPosition.LEFT_TOP, UnitPosition.RIGHT_TOP, UnitPosition.LEFT_BOTTOM, UnitPosition.RIGHT_BOTTOM -> {
                relativeGapWidth = 0.05f //gap size between text and unit
                relativeGapHeight = 0.025f //gap size between text and unit
                relativeHeight = 0.55f * relativeUniteSize
                relativeWidth = 0.3f * relativeUniteSize
            }
            else -> {
                relativeGapWidth = 0.05f
                relativeGapHeight = 0.025f
                relativeHeight = 0.55f * relativeUniteSize
                relativeWidth = 0.3f * relativeUniteSize
            }
        }

        val unitGapWidthHalf = mOuterTextBounds.width() * relativeGapWidth / 2f
        val unitWidth = (mOuterTextBounds.width() * relativeWidth)

        val unitGapHeightHalf = mOuterTextBounds.height() * relativeGapHeight / 2f
        val unitHeight = (mOuterTextBounds.height() * relativeHeight)


        var update = false
        //Draw Text
        if (mIsAutoColorEnabled) {
            mTextPaint.setColor(calcTextColor(mCurrentValue.toDouble()))
        }

        //set text
        val text: String?
        when (mTextMode) {
            TextMode.TEXT -> text = if (mText != null) mText else ""
            TextMode.PERCENT -> text = decimalFormat.format((100f / maxValue * mCurrentValue).toDouble()) + "%"
            TextMode.VALUE -> text = decimalFormat.format(mCurrentValue.toDouble()) + "%"
            else -> text = if (mText != null) mText else ""
        }


        // only re-calc position and size if string length changed
        if (mTextLength != text!!.length) {

            update = true
            mTextLength = text!!.length
            if (mTextLength == 1) {
                mOuterTextBounds = getInnerCircleRect(mCircleBounds)
                mOuterTextBounds = RectF(mOuterTextBounds.left + (mOuterTextBounds.width() * 0.1f), mOuterTextBounds.top, mOuterTextBounds.right - (mOuterTextBounds.width() * 0.1f), mOuterTextBounds.bottom)
            } else {
                mOuterTextBounds = getInnerCircleRect(mCircleBounds)
            }
            if (isAutoTextSize) {
                setTextSizeAndTextBoundsWithAutoTextSize(unitGapWidthHalf, unitWidth, unitGapHeightHalf, unitHeight, text)

            } else {
                setTextSizeAndTextBoundsWithFixedTextSize(text)
            }
        }


        if (DEBUG) {
            val rectPaint = Paint()
            rectPaint.setColor(Color.MAGENTA)
            canvas.drawRect(mOuterTextBounds, rectPaint)
            rectPaint.setColor(Color.GREEN)
            canvas.drawRect(mActualTextBounds, rectPaint)

        }

        canvas.drawText(text!!, mActualTextBounds.left - (mTextPaint.getTextSize() * 0.02f), mActualTextBounds.bottom, mTextPaint)

        if (isUnitVisible) {


            if (mIsAutoColorEnabled) {
                mUnitTextPaint.setColor(calcTextColor(mCurrentValue.toDouble()))
            }
            if (update) {
                //calc unit text position
                if (isAutoTextSize) {
                    setUnitTextBoundsAndSizeWithAutoTextSize(unitGapWidthHalf, unitWidth, unitGapHeightHalf, unitHeight)

                } else {
                    setUnitTextBoundsAndSizeWithFixedTextSize(unitGapWidthHalf * 2f, unitGapHeightHalf * 2f)
                }


            }

            if (DEBUG) {
                val rectPaint = Paint()
                rectPaint.setColor(Color.RED)
                canvas.drawRect(mUnitBounds, rectPaint)
            }

            canvas.drawText(unit!!, mUnitBounds.left - (mUnitTextPaint.getTextSize() * 0.02f), mUnitBounds.bottom, mUnitTextPaint)
        }
    }

    private fun drawBar(_canvas: Canvas, _degrees: Float) {
        if (!isShowBlock) {
            _canvas.drawArc(mCircleBounds, startAngle.toFloat(), _degrees, false, mBarPaint)
        } else {
            drawBlocks(_canvas, mCircleBounds, startAngle.toFloat(), _degrees, false, mBarPaint)
        }

    }

    //endregion draw
    //----------------------------------


    /**
     * Turn off spinning mode
     */
    fun stopSpinning() {
        mAnimationHandler.sendEmptyMessage(AnimationMsg.STOP_SPINNING.ordinal)
    }

    /**
     * Puts the view in spin mode
     */
    fun spin() {
        mAnimationHandler.sendEmptyMessage(AnimationMsg.START_SPINNING.ordinal)
    }


    //----------------------------------
    //region touch input
    public override fun onTouchEvent(event: MotionEvent): Boolean {

        if (isSeekModeEnabled == false) {
            return super.onTouchEvent(event)
        }

        when (event.getActionMasked()) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_UP -> {
                mTouchEventCount = 0
                val point = PointF(event.getX(), event.getY())
                val angle = normalizeAngle(Math.round(calcRotationAngleInDegrees(mCenter!!, point) - startAngle).toFloat())
                setValueAnimated(maxValue / 360f * angle, 800)
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                mTouchEventCount++
                if (mTouchEventCount > 5) { //touch/move guard
                    val point = PointF(event.getX(), event.getY())
                    val angle = normalizeAngle(Math.round(calcRotationAngleInDegrees(mCenter!!, point) - startAngle).toFloat())
                    setValue(maxValue / 360f * angle)
                    return true
                } else {
                    return false
                }

            }
            MotionEvent.ACTION_CANCEL -> {
                mTouchEventCount = 0
                return false
            }
        }


        return super.onTouchEvent(event)
    }


    //endregion touch input
    //----------------------------------


    //-----------------------------------
    //region listener for progress change


    interface OnProgressChangedListener {
        fun onProgressChanged(value: Float)
    }

    companion object {

        /**
         * The log tag.
         */
        private val TAG = "CircleView"
        private val DEBUG = false

        private fun calcTextSizeForRect(_text: String, _textPaint: Paint, _rectBounds: RectF): Float {

            val matrix = Matrix()
            val textBoundsTmp = Rect()
            //replace ones because for some fonts the 1 takes less space which causes issues
            val text = _text.replace('1', '0')

            //get current mText bounds
            _textPaint.getTextBounds(text, 0, text.length, textBoundsTmp)

            val textBoundsTmpF = RectF(textBoundsTmp)

            matrix.setRectToRect(textBoundsTmpF, _rectBounds, Matrix.ScaleToFit.CENTER)
            val values = FloatArray(9)
            matrix.getValues(values)
            return _textPaint.getTextSize() * values[Matrix.MSCALE_X]


        }

        /**
         * @param _angle The angle in degree to normalize
         * @return the angle between 0 (EAST) and 360
         */
        private fun normalizeAngle(_angle: Float): Float {
            return (((_angle % 360) + 360) % 360)
        }

        /**
         * Calculates the angle from centerPt to targetPt in degrees.
         * The return should range from [0,360), rotating CLOCKWISE,
         * 0 and 360 degrees represents EAST,
         * 90 degrees represents SOUTH, etc...
         *
         *
         * Assumes all points are in the same coordinate space.  If they are not,
         * you will need to call SwingUtilities.convertPointToScreen or equivalent
         * on all arguments before passing them  to this function.
         *
         * @param centerPt Point we are rotating around.
         * @param targetPt Point we want to calculate the angle to.
         * @return angle in degrees.  This is the angle from centerPt to targetPt.
         */
        fun calcRotationAngleInDegrees(centerPt: PointF, targetPt: PointF): Double {
            // calculate the angle theta from the deltaY and deltaX values
            // (atan2 returns radians values from [-PI,PI])
            // 0 currently points EAST.
            // NOTE: By preserving Y and X param order to atan2,  we are expecting
            // a CLOCKWISE angle direction.
            val theta = Math.atan2((targetPt.y - centerPt.y).toDouble(), (targetPt.x - centerPt.x).toDouble())

            // rotate the theta angle clockwise by 90 degrees
            // (this makes 0 point NORTH)
            // NOTE: adding to an angle rotates it clockwise.
            // subtracting would rotate it counter-clockwise
            //        theta += Math.PI/2.0;

            // convert from radians to degrees
            // this will give you an angle from [0->270],[-180,0]
            var angle = Math.toDegrees(theta)

            // convert to positive range [0-360)
            // since we want to prevent negative angles, adjust them now.
            // we can assume that atan2 will not return a negative value
            // greater than one partial rotation
            if (angle < 0) {
                angle += 360.0
            }

            return angle
        }
    }

    //endregion listener for progress change
    //--------------------------------------

}
/**
 * Sets the value of the circle view with an animation.
 * The current value is used as the start value of the animation
 *
 * @param _valueTo value after animation
 */


