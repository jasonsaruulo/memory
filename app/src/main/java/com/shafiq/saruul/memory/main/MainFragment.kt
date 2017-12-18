package com.shafiq.saruul.memory.main

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
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
    @BindView(R.id.main_game_board)
    lateinit var gameBoard: TableLayout
    @BindView(R.id.main_new_game)
    lateinit var newGame: View

    override fun onCreateView(
            inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater!!.inflate(R.layout.fragment_main, container, false)
        ButterKnife.bind(this, view)
        var i = 0
        while (i < gameBoard.childCount) {
            val row = gameBoard.getChildAt(i) as TableRow
            var j = 0
            while (j < row.childCount) {
                val memoryCard = row.getChildAt(j) as MemoryCardView
                memoryCard.setOnClickListener({
                    presenter.memoryCardClicked(memoryCards.indexOf(memoryCard))
                })
                memoryCards.add(memoryCard)
                j++
            }
            i++
        }
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
        gameBoard.visibility = View.INVISIBLE
        newGame.visibility = View.VISIBLE
    }

    override fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        permissionExplanation.visibility = View.INVISIBLE
        gameBoard.visibility = View.INVISIBLE
        newGame.visibility = View.INVISIBLE
    }

    override fun showGameBoard() {
        progressBar.visibility = View.INVISIBLE
        permissionExplanation.visibility = View.INVISIBLE
        gameBoard.visibility = View.VISIBLE
        newGame.visibility = View.VISIBLE
    }

    override fun setImage(memoryCardIndex: Int, bitmap: Bitmap?) {
        memoryCards[memoryCardIndex].content.setImageBitmap(bitmap)
    }

    @OnClick(R.id.main_new_game)
    fun newGame() {
        presenter.newGame()
    }

    override fun flipMemoryCard(memoryCardIndex: Int) {
        memoryCards[memoryCardIndex].flip()
    }
}
