package com.shafiq.saruul.memory.main

import com.shafiq.saruul.memory.BasePresenter

interface MainContract {

    interface View {

        fun loadImage(memoryCardIndex: Int, filePath: String)

        fun showPermissionExplanation()

        fun showProgressBar()

        fun showGameBoard()

        fun showGameStoppingDialog()

        fun showMainMenu()

        fun flipMemoryCard(memoryCardIndex: Int)

        fun numberOfTurns(numberOfTurns: Int)

        fun expandMemoryCard(memoryCardIndex: Int)

        fun minimizeMemoryCard(memoryCardIndex: Int)
    }

    interface Presenter: BasePresenter<View> {

        fun newGame(numberOfMemoryCards: Int)

        fun stopGame()

        fun onRequestPermissionsResult(
                requestCode: Int, permissions: Array<out String>, grantResults: IntArray)

        fun onImageLoaded(success: Boolean, memoryCardIndex: Int)

        fun onMemoryCardClicked(memoryCardIndex: Int)

        /**
         * Tries to minimize the expanded view. Returns true if a minimization was started, false
         * otherwise (if view was not expanded).
         */
        fun onExpandedViewClicked(): Boolean

        /**
         * Returns true if the back click should be treated normally, false if there is another
         * logic handled by the presenter.
         */
        fun onBackPressed(): Boolean
    }
}