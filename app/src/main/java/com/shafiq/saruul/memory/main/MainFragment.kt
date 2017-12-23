package com.shafiq.saruul.memory.main

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.shafiq.saruul.memory.ActivityScoped
import com.shafiq.saruul.memory.R
import dagger.android.support.DaggerFragment
import javax.inject.Inject

@ActivityScoped
class MainFragment @Inject constructor(): DaggerFragment(), MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter
    @Inject
    lateinit var memoryCards: MutableList<MemoryCardView>
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
                memoryCard.setOnClickListener({
                    presenter.onMemoryCardClicked(memoryCards.indexOf(memoryCard))
                })
                memoryCards.add(memoryCard)
                j++
            }
            i++
        }
        numberOfTurns(0)
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
        Glide.with(this)
                .load(filePath)
                .listener(listener)
                .into(memoryCards[memoryCardIndex].content)
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
}
