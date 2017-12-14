package com.saruul.shafiq.memory.main

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import com.saruul.shafiq.memory.R

class MemoryCardView: FrameLayout {

    @BindView(R.id.memory_card_view_placeholder)
    lateinit var placeholder: ImageView
    @BindView(R.id.memory_card_view_content)
    lateinit var content: ImageView

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.memory_card_view, this, true)
        ButterKnife.bind(this, view)
    }
}