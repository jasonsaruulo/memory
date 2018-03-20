package com.shafiq.saruul.memory.main

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TableRow
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.shafiq.saruul.memory.ActivityScoped
import com.shafiq.saruul.memory.GlideApp
import com.shafiq.saruul.memory.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.memory_card_view.view.*
import javax.inject.Inject

@ActivityScoped
class MainFragment @Inject constructor(): DaggerFragment(), MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter
    @Inject
    lateinit var memoryCards: MutableList<MemoryCardView>
    @Inject
    lateinit var alertDialogBuilder: AlertDialog.Builder

    private var currentExpandedImageAnimator: Animator? = null
    private var expandImageAnimationDuration: Long? = null
    private val numberOfMemoryCardsPerRow = 6

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        expandImageAnimationDuration =
                resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
        if (memoryCards.isEmpty()) {
            var i = 0
            while (i < main_game_board.childCount) {
                val row = main_game_board.getChildAt(i) as TableRow
                var j = 0
                while (j < row.childCount) {
                    val memoryCard = row.getChildAt(j) as MemoryCardView
                    memoryCard.setOnClickListener {
                        presenter.onMemoryCardClicked(memoryCards.indexOf(memoryCard))
                    }
                    memoryCards.add(memoryCard)
                    j++
                }
                i++
            }
            main_new_game.setOnClickListener {
                newGame()
            }
        }
    }

    override fun onPause() {
        presenter.dropView()
        super.onPause()
    }

    override fun showPermissionExplanation() {
        main_progress_bar.visibility = View.INVISIBLE
        main_permission_explanation.visibility = View.VISIBLE
        main_number_of_turns.visibility = View.INVISIBLE
        main_game_board.visibility = View.INVISIBLE
        main_number_of_memory_cards_description.visibility = View.VISIBLE
        main_number_of_memory_cards.visibility = View.VISIBLE
        main_new_game.visibility = View.VISIBLE
    }

    override fun showProgressBar() {
        main_progress_bar.visibility = View.VISIBLE
        main_permission_explanation.visibility = View.INVISIBLE
        main_number_of_turns.visibility = View.INVISIBLE
        main_game_board.visibility = View.INVISIBLE
        main_number_of_memory_cards_description.visibility = View.INVISIBLE
        main_number_of_memory_cards.visibility = View.INVISIBLE
        main_new_game.visibility = View.INVISIBLE
    }

    override fun showGameBoard() {
        main_progress_bar.visibility = View.INVISIBLE
        main_permission_explanation.visibility = View.INVISIBLE
        main_number_of_turns.visibility = View.VISIBLE
        main_game_board.visibility = View.VISIBLE
        main_number_of_memory_cards_description.visibility = View.INVISIBLE
        main_number_of_memory_cards.visibility = View.INVISIBLE
        main_new_game.visibility = View.INVISIBLE
    }

    override fun showGameStoppingDialog() {
        alertDialogBuilder.setTitle(R.string.main_game_stopping_title)
        alertDialogBuilder.setMessage(R.string.main_game_stopping_message)
        alertDialogBuilder.setPositiveButton(R.string.main_game_stopping_positive_button, { _, _ ->
            presenter.stopGame()
        })
        alertDialogBuilder.setNegativeButton(R.string.main_game_stopping_negative_button, null)
        alertDialogBuilder.show()
    }

    override fun showMainMenu() {
        main_progress_bar.visibility = View.INVISIBLE
        main_permission_explanation.visibility = View.INVISIBLE
        main_number_of_turns.visibility = View.INVISIBLE
        main_game_board.visibility = View.INVISIBLE
        main_number_of_memory_cards_description.visibility = View.VISIBLE
        main_number_of_memory_cards.visibility = View.VISIBLE
        main_new_game.visibility = View.VISIBLE
    }

    override fun loadImage(memoryCardIndex: Int, filePath: String) {
        val listener = object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                presenter.onImageLoaded(false, memoryCardIndex)
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                presenter.onImageLoaded(true, memoryCardIndex)
                return false
            }
        }
        memoryCards[memoryCardIndex].loadImage(filePath, listener)
    }

    private fun newGame() {
        var i = 0
        while (i < main_number_of_memory_cards.selectedItemPosition + 1) {
            val row = main_game_board.getChildAt(i) as TableRow
            row.visibility = View.VISIBLE
            i++
        }
        while (i < main_game_board.childCount) {
            val row = main_game_board.getChildAt(i) as TableRow
            row.visibility = View.GONE
            i++
        }
        presenter.newGame((main_number_of_memory_cards.selectedItemPosition + 1) * numberOfMemoryCardsPerRow)
    }

    override fun flipMemoryCard(memoryCardIndex: Int) {
        memoryCards[memoryCardIndex].flip()
    }

    override fun numberOfTurns(numberOfTurns: Int) {
        main_number_of_turns.text = resources.getString(R.string.main_number_of_turns, numberOfTurns)
    }

    override fun expandMemoryCard(memoryCardIndex: Int) {
        val memoryCard = memoryCards[memoryCardIndex]

        currentExpandedImageAnimator?.cancel()
        GlideApp.with(memoryCard)
                .load(memoryCard.filePath)
                .placeholder(memoryCard.memory_card_view_content.drawable)
                .into(main_expanded_image)

        val startBounds = Rect()
        val finalBounds = Rect()

        val startScale = adjustBounds(memoryCardIndex, startBounds, finalBounds)
        memoryCard.alpha = 0f
        main_expanded_image.visibility = View.VISIBLE

        main_expanded_image.pivotX = 0f
        main_expanded_image.pivotY = 0f

        // For better readability
        val startLeft = startBounds.left.toFloat()
        val finalLeft = finalBounds.left.toFloat()
        val startTop = startBounds.top.toFloat()
        val finalTop = finalBounds.top.toFloat()

        val set = AnimatorSet()
        set.play(ObjectAnimator.ofFloat(main_expanded_image, View.X, startLeft, finalLeft))
                .with(ObjectAnimator.ofFloat(main_expanded_image, View.Y, startTop, finalTop))
                .with(ObjectAnimator.ofFloat(main_expanded_image, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(main_expanded_image, View.SCALE_Y, startScale, 1f))
        if (expandImageAnimationDuration != null) {
            set.duration = expandImageAnimationDuration!!
        }
        set.interpolator = DecelerateInterpolator()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                currentExpandedImageAnimator = null
            }

            override fun onAnimationCancel(p0: Animator?) {
                currentExpandedImageAnimator = null
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
        set.start()
        currentExpandedImageAnimator = set
        main_expanded_image.setOnClickListener {
            presenter.onExpandedViewClicked()
        }
    }

    override fun minimizeMemoryCard(memoryCardIndex: Int) {
        currentExpandedImageAnimator?.cancel()

        val startBounds = Rect()
        val finalBounds = Rect()

        val startScale = adjustBounds(memoryCardIndex, startBounds, finalBounds)
        // For better readability
        val startLeft = startBounds.left.toFloat()
        val startTop = startBounds.top.toFloat()

        val set = AnimatorSet()
        set.play(ObjectAnimator.ofFloat(main_expanded_image, View.X, startLeft))
                .with(ObjectAnimator.ofFloat(main_expanded_image, View.Y, startTop))
                .with(ObjectAnimator.ofFloat(main_expanded_image, View.SCALE_X, startScale))
                .with(ObjectAnimator.ofFloat(main_expanded_image, View.SCALE_Y, startScale))
        if (expandImageAnimationDuration != null) {
            set.duration = expandImageAnimationDuration!!
        }
        set.interpolator = DecelerateInterpolator()
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationEnd(p0: Animator?) {
                onMinimizeAnimationEnd(memoryCardIndex)
            }

            override fun onAnimationCancel(p0: Animator?) {
                onMinimizeAnimationEnd(memoryCardIndex)
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }

            override fun onAnimationStart(p0: Animator?) {
            }
        })
        set.start()
        currentExpandedImageAnimator = set
    }

    private fun onMinimizeAnimationEnd(memoryCardIndex: Int) {
        memoryCards[memoryCardIndex].alpha = 1f
        main_expanded_image.visibility = View.GONE
        currentExpandedImageAnimator = null
    }


    /**
     * Adjusts bounds based on given memory card view and returns the start scale for the expanded
     * image view.
     */
    private fun adjustBounds(memoryCardIndex: Int, startBounds: Rect, finalBounds: Rect): Float {
        val memoryCard = memoryCards[memoryCardIndex]
        val globalOffset = Point()
        memoryCard.getGlobalVisibleRect(startBounds)
        main_container.getGlobalVisibleRect(finalBounds, globalOffset)
        startBounds.offset(-globalOffset.x, -globalOffset.y)
        finalBounds.offset(-globalOffset.x, -globalOffset.y)
        val startScale: Float?
        if (finalBounds.width()/startBounds.height() > startBounds.width()/startBounds.height()) {
            startScale = startBounds.height().toFloat()/finalBounds.height().toFloat()
            val startWidth = startScale * finalBounds.width()
            val deltaWidth = (startWidth - startBounds.width())/2
            startBounds.left -= deltaWidth.toInt()
            startBounds.right += deltaWidth.toInt()
        } else {
            startScale = startBounds.width().toFloat() / finalBounds.width().toFloat()
            val startHeight = startScale * finalBounds.height()
            val deltaHeight = (startHeight - startBounds.height()) / 2
            startBounds.top -= deltaHeight.toInt()
            startBounds.bottom += deltaHeight.toInt()
        }
        return startScale
    }
}
