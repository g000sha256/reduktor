package ru.g000sha256.reduktor.demo.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class OutlineLinearLayout : LinearLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        clipToOutline = true
    }

}