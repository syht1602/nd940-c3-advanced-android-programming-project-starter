package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonColor = 0
    private var buttonLoadingColor = 0
    private var textColor = 0
    private var textLoadingColor = 0
    private var progressCount = 0F

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val loadingIconPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val animator = ValueAnimator.ofFloat(0f, 1f)

    private var title: String = ""
    private var text: String = ""
    private var textLoading: String = ""

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { p, old, new ->
        when (new) {
            is ButtonState.Loading -> {
                textPaint.color = textLoadingColor
                title = textLoading
                this.isEnabled = false
            }

            is ButtonState.Completed -> {
                textPaint.color = textColor
                title = text
                progressCount = 0f
                this.isEnabled = true
                if (animator != null && animator.isRunning) {
                    animator.end()
                }
                invalidate()
            }

            else -> {
                progressCount = 0f
            }
        }
    }

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingButton)
        buttonColor = typedArray.getColor(R.styleable.LoadingButton_btnColor, Color.BLUE)
        buttonLoadingColor =
            typedArray.getColor(R.styleable.LoadingButton_btnLoadingColor, Color.BLUE)
        textColor = typedArray.getColor(R.styleable.LoadingButton_btnTextColor, Color.WHITE)
        textLoadingColor =
            typedArray.getColor(R.styleable.LoadingButton_btnTextLoadingColor, Color.WHITE)
        text = typedArray.getString(R.styleable.LoadingButton_btnText) ?: ""
        textLoading = typedArray.getString(R.styleable.LoadingButton_btnTextLoading) ?: ""
        textPaint.apply {
            textSize = 40.0f
            textAlign = Paint.Align.CENTER
            typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            style = Paint.Style.FILL
        }
        loadingIconPaint.color = textLoadingColor
        buttonState = ButtonState.Completed

        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the background
        val splitter = widthSize * progressCount
        paint.color = buttonLoadingColor
        canvas.drawRect(0f, 0f, splitter, height.toFloat(), paint)
        paint.color = buttonColor
        canvas.drawRect(splitter, 0f, widthSize.toFloat(), height.toFloat(), paint)

        paint.color = textLoadingColor
        val xPos = width / 2
        val yPos = (height / 2 - (textPaint.descent() + textPaint.ascent()) / 2).toInt()
        canvas.drawText(title, xPos.toFloat(), yPos.toFloat(), textPaint)

        val rectTextBounds = Rect()
        textPaint.getTextBounds(title, 0, title.length, rectTextBounds)
        val textHeight = rectTextBounds.height()
        canvas.translate(
            xPos + rectTextBounds.width() / 2 + 20f,
            ((height - textHeight) / 2).toFloat()
        )

        val rectIcon = RectF()
        rectIcon.set(0f, 0f, textHeight.toFloat(), textHeight.toFloat())
        canvas.drawArc(rectIcon, 0f, 360 * progressCount, true, loadingIconPaint)
    }

    fun updateButtonState(buttonState: ButtonState) {
        this.buttonState = buttonState
    }
    fun animateButton(repeat: Int) {
        animator.duration = 2000
        animator.repeatMode = ValueAnimator.RESTART
        animator.repeatCount = repeat
        animator.addUpdateListener {
            progressCount = it.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                buttonState = ButtonState.Completed
            }
        })
        animator.start()
    }

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

}
