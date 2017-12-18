package com.shafiq.saruul.memory.main

import android.animation.Animator
import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import butterknife.BindView
import butterknife.ButterKnife
import com.shafiq.saruul.memory.R

class MemoryCardView: FrameLayout {

    @BindView(R.id.memory_card_view_placeholder)
    lateinit var placeholder: ImageView
    @BindView(R.id.memory_card_view_content)
    lateinit var content: ImageView
    var flipping = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        val view = LayoutInflater.from(context)
                .inflate(R.layout.memory_card_view, this, true)
        ButterKnife.bind(this, view)
    }

    fun flip() {
        if (content.drawable == null) {
            return
        }
        if (flipping) {
            return
        }
        flipping = true
        val listener = object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                flipping = false
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        }
        if (content.alpha == 0f) {
            val leftIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_in)
            leftIn.setTarget(content)
            val leftOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_left_out)
            leftOut.setTarget(placeholder)
            leftOut.addListener(listener)
            leftIn.start()
            leftOut.start()
        } else {
            val rightIn = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_in)
            rightIn.setTarget(placeholder)
            val rightOut = AnimatorInflater.loadAnimator(context, R.animator.card_flip_right_out)
            rightOut.setTarget(content)
            rightOut.addListener(listener)
            rightIn.start()
            rightOut.start()
        }
    }
}
