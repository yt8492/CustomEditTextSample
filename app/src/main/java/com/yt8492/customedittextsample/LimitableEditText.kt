package com.yt8492.customedittextsample

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.util.AttributeSet
import android.util.Log
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
                val overLimitColor = it.getColor(R.styleable.LimitableEditText_limitBackgroundColor, Color.RED)
                overLimitColorSpan =
                    BackgroundColorSpan(
                        Color.argb(20, Color.red(overLimitColor), Color.green(overLimitColor), Color.blue(overLimitColor))
                    )
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
                Log.d("debug", "after")
                if (s == null) return
                checkThresholdLength(s)
                if (s.getSpans(0, s.length, Any::class.java)
                        ?.map { it::class.java.name }
                        ?.contains(COMPOSING_TEXT_NAME) == false) {
                    checkLimitLength(s)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                checkText()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
;
        })
    }

    private fun checkText() {
        val editable = text ?: return
        checkThresholdLength(editable)
        checkLimitLength(editable)
    }

    private fun checkThresholdLength(editable: Editable) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, if (editable.length <= textSizeThresholdLength) textSizeDefault else textSizeMini)
    }

    private fun checkLimitLength(editable: Editable) {
        if (editable.length > limitTextLength) {
            editable.setSpan(overLimitColorSpan, limitTextLength, editable.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }


    fun getTextLengthDiff() = limitTextLength - text.length

    companion object {
        const val COMPOSING_TEXT_NAME = "android.view.inputmethod.ComposingText"
    }
}