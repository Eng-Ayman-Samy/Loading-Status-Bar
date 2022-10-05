package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var frame = Rect(0, 0, 0, 0)
    private var backColor: Int =
        0 //= ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private var animationBarColor: Int =
        0// = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private var textColor: Int =
        0 //= ResourcesCompat.getColor(resources, R.color.white, null)
    private var circleColor: Int =
        0//= ResourcesCompat.getColor(resources, R.color.colorAccent, null)
    private var textDownload: String = ""//resources.getString(R.string.download)
    private var textLoading: String = ""// resources.getString(R.string.button_loading)
    private var textDraw = textDownload
    private var sweepAngle = 0f
    //private var textSizeValue = 60.0f
    //private var animatedWidth = 0


    //    private val paintColor = Paint().apply {
//        color = drawColor
//    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val valueAnimator = ValueAnimator().apply {
        duration = 2000
        repeatCount = ValueAnimator.INFINITE
        repeatMode = ValueAnimator.RESTART
    }

    var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->

//        textDraw = when (new) {
//            //ButtonState.Clicked -> {}
//            ButtonState.Loading -> {
//                textLoading
//            }
//            else -> {
//                textDownload
//            }
//        }
        if (new == ButtonState.Loading) {
            textDraw = textLoading
            valueAnimator.setObjectValues(0, width)
            //Log.d("logTest", "clicked w = ${valueAnimator.values.size}")
            valueAnimator.addUpdateListener {
                val animatedWidth = it.animatedValue as Int
                frame = Rect(0, 0, animatedWidth, height)
                sweepAngle = animatedWidth * 360f / width
                //Log.d("logTest", "called")
                invalidate()
            }
            valueAnimator.start()
        } else {
            valueAnimator.cancel()
            textDraw = textDownload
            invalidate()
        }
    }


    init {
        isClickable = true
//        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
//            backColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
//            circleColor = getColor(R.styleable.LoadingButton_arcColor, 0)
//            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
//            animationBarColor = getColor(R.styleable.LoadingButton_animationBarColor, 0)
//            textDownload = getString(R.styleable.LoadingButton_text) ?: ""
//            textLoading = getString(R.styleable.LoadingButton_textShowAtAnimation) ?: ""
//        }
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                backColor = getColor(R.styleable.LoadingButton_backgroundColor, 0)
                circleColor = getColor(R.styleable.LoadingButton_arcColor, 0)
                textColor = getColor(R.styleable.LoadingButton_textColor, 0)
                animationBarColor = getColor(R.styleable.LoadingButton_animationBarColor, 0)
                textDownload = getString(R.styleable.LoadingButton_text).toString()
                textLoading = getString(R.styleable.LoadingButton_textShowAtAnimation).toString()
            } finally {
                textDraw = textDownload
                recycle()//recycle typed array
            }
        }
    }

    private val bound = Rect()
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw backgroundColor
        canvas?.drawColor(backColor)
        //draw animation
        paint.getTextBounds(textDraw, 0, textDraw.length, bound)
        if (buttonState == ButtonState.Loading) {
            paint.color = animationBarColor
            canvas?.drawRect(frame, paint)
            paint.color = circleColor
            canvas?.drawArc(
                arcRect(bound.width()),
                0f,
                sweepAngle,
                true,
                paint
            )
        }
        //draw text
        paint.color = textColor
        canvas?.drawText(textDraw, width / 2f, height / 2f - bound.exactCenterY(), paint)
    }

    private fun arcRect(w: Int) = RectF(
        width / 2f + 10f + w / 2,
        height / 4f,
        width / 2f + 10f + w / 2 + height / 2,
        height * 3f / 4
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        super.onSizeChanged(w, h, oldw, oldh)
//        //frame = Rect(0, 0, width, height)
//    }
}