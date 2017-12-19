package com.shafiq.saruul.memory.main

import android.content.pm.PackageManager
import com.shafiq.saruul.memory.handlers.PermissionHandler
import com.shafiq.saruul.memory.handlers.StorageHandler
import java.util.Random
import javax.inject.Inject

class MainPresenter @Inject constructor(val random: Random,
                                        val filePaths: MutableList<String>,
                                        val unusedMemoryCardIndexes: MutableList<Int>,
                                        val permissionHandler: PermissionHandler,
                                        val storageHandler: StorageHandler):
        MainContract.Presenter {

    private var view: MainContract.View? = null
    private val numberOfMemoryCards = 12
    private var numberOfImagesLoaded = 0

    override fun takeView(view: MainContract.View) {
        this.view = view
    }

    override fun dropView() {
        view = null
    }

    override fun showPermissionExplanation() {
        view?.showPermissionExplanation()
    }

    override fun showProgressBar() {
        view?.showProgressBar()
    }

    override fun showGameBoard() {
        view?.showGameBoard()
    }

    override fun newGame() {
        if (!permissionHandler.permissionReadExternalStorageGranted()) {
            if (permissionHandler.shouldShowRequestPermissionRationale()) {
                showPermissionExplanation()
            } else {
                permissionHandler.requestReadExternalStoragePermission()
            }
        } else {
            loadFilePaths()
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (grantResults.isEmpty()) {
            return
        }
        when (requestCode) {
            PermissionHandler.readExternalStoragePermissionRequest ->
                if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    loadFilePaths()
                } else {
                    showPermissionExplanation()
                }
        }
    }

    override fun loadFilePaths() {
        showProgressBar()
        if (filePaths.isEmpty() || filePaths.size < numberOfMemoryCards) {
            filePaths.addAll(storageHandler.getAllImagePaths())
        }
        loadImages()
    }

    override fun loadImages() {
        numberOfImagesLoaded = 0
        if (filePaths.size >= numberOfMemoryCards) {
            resetUnusedIndexes()
            var i = 0
            while (i < numberOfMemoryCards) {
                loadImage(unusedMemoryCardIndex())
                i++
            }
        } else {
            // TODO: Show error message
        }
    }

    private fun resetUnusedIndexes() {
        var i = 0
        unusedMemoryCardIndexes.clear()
        while (i < numberOfMemoryCards) {
            unusedMemoryCardIndexes.add(i)
            i++
        }
    }

    private fun loadImage(memoryCardIndex: Int) {
        if (filePaths.isEmpty()) {
            // TODO: Show error message
            showGameBoard()
            return
        }
        view?.loadImage(memoryCardIndex, unusedFilePath())
    }

    override fun onImageLoaded(success: Boolean, memoryCardIndex: Int) {
        if (success) {
            numberOfImagesLoaded++
            if (numberOfImagesLoaded == numberOfMemoryCards) {
                showGameBoard()
                // TODO: Start the game
            }
        } else {
            // TODO: Load image
            loadImage(memoryCardIndex)
        }
    }

    private fun unusedFilePath(): String {
        val randomIndex = randomIndex(filePaths.size)
        val filePath = filePaths[randomIndex]
        filePaths.removeAt(randomIndex)
        return filePath
    }

    private fun unusedMemoryCardIndex(): Int {
        val randomIndex = randomIndex(unusedMemoryCardIndexes.size)
        val unusedMemoryCardIndex = unusedMemoryCardIndexes[randomIndex]
        unusedMemoryCardIndexes.removeAt(randomIndex)
        return unusedMemoryCardIndex
    }

    private fun randomIndex(max: Int): Int {
        return random.nextInt(max)
    }

    override fun onMemoryCardClicked(memoryCardIndex: Int) {
        view?.flipMemoryCard(memoryCardIndex)
    }
}
