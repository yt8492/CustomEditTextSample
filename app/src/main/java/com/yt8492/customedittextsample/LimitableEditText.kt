package com.yt8492.customedittextsample

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.EditText

class LimitableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : EditText(context, attrs, defStyleAttr) {

    private val textSizeThresholdLength: Int
    private val limitTextLength: Int
    private val textSizeMini: Float
    private val textSizeDefault: Float
    private val overLimitColorSpan: BackgroundColorSpan
    private val defaultColorSpan = BackgroundColorSpan(Color.TRANSPARENT)

    init {
        context.obtainStyledAttributes(attrs, R.styleable.LimitableEditText)
            .also {
                textSizeThresholdLength = it.getInteger(R.styleable.LimitableEditText_thresholdLength, -1)
                limitTextLength = it.getInteger(R.styleable.LimitableEditText_limitLength, -1)
                overLimitColorSpan =
                    BackgroundColorSpan(it.getColor(R.styleable.LimitableEditText_limitBackgroundColor, Color.RED))
                textSizeMini = it.getDimension(
                    R.styleable.LimitableEditText_textSizeMini,
                    resources.getDimension(R.dimen.textDefault)
                )
                textSizeDefault = it.getDimension(
                    R.styleable.LimitableEditText_textSizeDefault,
                    resources.getDimension(R.dimen.textDefault)
                )
            }.recycle()
        addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s == null) return
                setTextSize(TypedValue.COMPLEX_UNIT_PX, if (s.length <= textSizeThresholdLength) textSizeDefault else textSizeMini)
                Spannable.Factory.getInstance().newSpannable(this@LimitableEditText.text).apply {
                    if (s.length <= limitTextLength) {
                        removeSpan(overLimitColorSpan)
                        s.setSpan(defaultColorSpan, 0, s.length, getSpanFlags(defaultColorSpan))
                    } else {
                        removeSpan(defaultColorSpan)
                        s.setSpan(overLimitColorSpan, limitTextLength, s.length, getSpanFlags(overLimitColorSpan))
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    fun getTextLengthDiff() = limitTextLength - text.length

    companion object {
        const val COMPOSING_TEXT_NAME = "android.view.inputmethod.ComposingText"
    }
}