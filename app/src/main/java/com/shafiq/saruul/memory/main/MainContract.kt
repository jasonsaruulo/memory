package com.shafiq.saruul.memory.main

import com.shafiq.saruul.memory.BasePresenter

interface MainContract {

    interface View {

        fun loadImage(memoryCardIndex: Int, filePath: String)

        fun showPermissionExplanation()

        fun showProgressBar()

        fun showGameBoard()

        fun flipMemoryCard(memoryCardIndex: Int)

        fun numberOfTurns(numberOfTurns: Int)
    }

    interface Presenter: BasePresenter<View> {

        fun showPermissionExplanation()

        fun showProgressBar()

        fun showGameBoard()

        fun newGame()

        fun onRequestPermissionsResult(
                requestCode: Int, permissions: Array<out String>, grantResults: IntArray)

        fun loadFilePaths()

        fun loadImages()

        fun onImageLoaded(success: Boolean, memoryCardIndex: Int)

        fun onMemoryCardClicked(memoryCardIndex: Int)
    }
}