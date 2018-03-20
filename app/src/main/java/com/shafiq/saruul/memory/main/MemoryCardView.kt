package com.shafiq.saruul.memory.main

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.bumptech.glide.request.RequestListener
import com.shafiq.saruul.memory.GlideApp
import com.shafiq.saruul.memory.R
import kotlinx.android.synthetic.main.memory_card_view.view.*

class MemoryCardView: FrameLayout {

    private val leftIn: Animator
    private val leftOut: Animator
    private val rightIn: Animator
    private val rightOut: Animator
    var filePath: String? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.memory_card_view, this, true)
        leftIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in)
        leftIn.setTarget(memory_card_view_content)
        leftOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out)
        leftOut.setTarget(memory_card_view_placeholder)
        rightIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in)
        rightIn.setTarget(memory_card_view_placeholder)
        rightOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out)
        rightOut.setTarget(memory_card_view_content)
    }

    fun flip() {
        if (memory_card_view_content.drawable == null) {
            return
        }
        leftIn.end()
        leftOut.end()
        rightIn.end()
        rightOut.end()
        if (memory_card_view_content.alpha == 0f) {
            leftIn.start()
            leftOut.start()
        } else {
            rightIn.start()
            rightOut.start()
        }
    }

    fun loadImage(filePath: String, listener: RequestListener<Drawable>) {
        this.filePath = filePath
        GlideApp.with(this)
                .load(filePath)
                .listener(listener)
                .into(memory_card_view_content)
    }
}
