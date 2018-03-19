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
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.shafiq.saruul.memory.ActivityScoped
import com.shafiq.saruul.memory.GlideApp
import com.shafiq.saruul.memory.R
import dagger.android.support.DaggerFragment
import kotlinx.android.synthetic.main.fragment_main.*
import javax.inject.Inject

@ActivityScoped
class MainFragment @Inject constructor(): DaggerFragment(), MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter
    @Inject
    lateinit var memoryCards: MutableList<MemoryCardView>
    @Inject
    lateinit var alertDialogBuilder: AlertDialog.Builder
    @BindView(R.id.main_progress_bar)
    lateinit var progressBar: View
    @BindView(R.id.main_permission_explanation)
    lateinit var permissionExplanation: View
    @BindView(R.id.main_number_of_turns)
    lateinit var numberOfTurns: TextView
    @BindView(R.id.main_game_board)
    lateinit var gameBoard: TableLayout
    @BindView(R.id.main_new_game)
    lateinit var newGame: View
    @BindView(R.id.main_expanded_image)
    lateinit var expandedImage: ImageView
    private var currentExpandedImageAnimator: Animator? = null
    private var expandImageAnimationDuration: Long? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        ButterKnife.bind(this, view)
        var i = 0
        while (i < gameBoard.childCount) {
            val row = gameBoard.getChildAt(i) as TableRow
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
        numberOfTurns(0)
        expandImageAnimationDuration =
                resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        return view
    }

    override fun onResume() {
        super.onResume()
        presenter.takeView(this)
    }

    override fun onPause() {
        presenter.dropView()
        super.onPause()
    }

    override fun showPermissionExplanation() {
        progressBar.visibility = View.INVISIBLE
        permissionExplanation.visibility = View.VISIBLE
        numberOfTurns.visibility = View.INVISIBLE
        gameBoard.visibility = View.INVISIBLE
        newGame.visibility = View.VISIBLE
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        permissionExplanation.visibility = View.INVISIBLE
        numberOfTurns.visibility = View.INVISIBLE
        gameBoard.visibility = View.INVISIBLE
        newGame.visibility = View.INVISIBLE
    }

    override fun showGameBoard() {
        progressBar.visibility = View.INVISIBLE
        permissionExplanation.visibility = View.INVISIBLE
        numberOfTurns.visibility = View.VISIBLE
        gameBoard.visibility = View.VISIBLE
        newGame.visibility = View.INVISIBLE
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
        progressBar.visibility = View.INVISIBLE
        permissionExplanation.visibility = View.INVISIBLE
        numberOfTurns.visibility = View.INVISIBLE
        gameBoard.visibility = View.INVISIBLE
        newGame.visibility = View.VISIBLE
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

    @OnClick(R.id.main_new_game)
    fun newGame() {
        presenter.newGame()
    }

    override fun flipMemoryCard(memoryCardIndex: Int) {
        memoryCards[memoryCardIndex].flip()
    }

    override fun numberOfTurns(numberOfTurns: Int) {
        this.numberOfTurns.text = resources.getString(R.string.main_number_of_turns, numberOfTurns)
    }

    override fun expandMemoryCard(memoryCardIndex: Int) {
        val memoryCard = memoryCards[memoryCardIndex]

        currentExpandedImageAnimator?.cancel()
        GlideApp.with(memoryCard)
                .load(memoryCard.filePath)
                .placeholder(memoryCard.content.drawable)
                .into(expandedImage)

        val startBounds = Rect()
        val finalBounds = Rect()

        val startScale = adjustBounds(memoryCardIndex, startBounds, finalBounds)
        memoryCard.alpha = 0f
        expandedImage.visibility = View.VISIBLE

        expandedImage.pivotX = 0f
        expandedImage.pivotY = 0f

        // For better readability
        val startLeft = startBounds.left.toFloat()
        val finalLeft = finalBounds.left.toFloat()
        val startTop = startBounds.top.toFloat()
        val finalTop = finalBounds.top.toFloat()

        val set = AnimatorSet()
        set.play(ObjectAnimator.ofFloat(expandedImage, View.X, startLeft, finalLeft))
                .with(ObjectAnimator.ofFloat(expandedImage, View.Y, startTop, finalTop))
                .with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_X, startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_Y, startScale, 1f))
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
        expandedImage.setOnClickListener {
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
        set.play(ObjectAnimator.ofFloat(expandedImage, View.X, startLeft))
                .with(ObjectAnimator.ofFloat(expandedImage, View.Y, startTop))
                .with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_X, startScale))
                .with(ObjectAnimator.ofFloat(expandedImage, View.SCALE_Y, startScale))
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
        expandedImage.visibility = View.GONE
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
