package com.shafiq.saruul.memory.main

import android.graphics.Bitmap
import com.shafiq.saruul.memory.BasePresenter

interface MainContract {

    interface View {

        fun setImage(memoryCardIndex: Int, bitmap: Bitmap?)

        fun showPermissionExplanation()

        fun showProgressBar()

        fun showGameBoard()

        fun flipMemoryCard(memoryCardIndex: Int)
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

        fun memoryCardClicked(memoryCardIndex: Int)
    }
}