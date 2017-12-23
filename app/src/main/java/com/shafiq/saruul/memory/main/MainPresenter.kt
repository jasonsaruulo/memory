package com.shafiq.saruul.memory.main

import android.content.pm.PackageManager
import com.shafiq.saruul.memory.handlers.PermissionHandler
import com.shafiq.saruul.memory.handlers.StorageHandler
import java.util.Random
import javax.inject.Inject

class MainPresenter @Inject constructor(private val random: Random,
                                        private val filePaths: MutableList<String>,
                                        private val unusedMemoryCardIndexes: MutableList<Int>,
                                        private val permissionHandler: PermissionHandler,
                                        private val storageHandler: StorageHandler):
        MainContract.Presenter {

    private var view: MainContract.View? = null
    private val numberOfMemoryCards = 12
    private var numberOfImagesLoaded = 0
    private val memoryCardIndexesMapping = mutableMapOf<Int, Int>()
    private val flippedMemoryCardIndexes = mutableSetOf<Int>()
    private val maxNumberOfFlippedMemoryCards = 2

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
                val filePath = unusedFilePath()
                val firstIndex = unusedMemoryCardIndex()
                val secondIndex = unusedMemoryCardIndex()
                memoryCardIndexesMapping[firstIndex] = secondIndex
                view?.loadImage(firstIndex, filePath)
                view?.loadImage(secondIndex, filePath)
                i += 2
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

    override fun onImageLoaded(success: Boolean, memoryCardIndex: Int) {
        if (success) {
            numberOfImagesLoaded++
            if (numberOfImagesLoaded == numberOfMemoryCards) {
                showGameBoard()
                // TODO: Start the game
            }
        } else {
            if (memoryCardIndexesMapping.contains(memoryCardIndex)) {
                if (filePaths.isEmpty()) {
                    // TODO: Show error
                } else {
                    // TODO: Load another image
                    val filePath = unusedFilePath()
                    val secondIndex = memoryCardIndexesMapping.getValue(memoryCardIndex)
                    view?.loadImage(memoryCardIndex, filePath)
                    view?.loadImage(secondIndex, filePath)
                }
            }
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
        val flipped = flippedMemoryCardIndexes.contains(memoryCardIndex)
        if (flippedMemoryCardIndexes.size >= maxNumberOfFlippedMemoryCards) {
            for (index in flippedMemoryCardIndexes) {
                view?.flipMemoryCard(index)
            }
            flippedMemoryCardIndexes.clear()
        }
        if (flipped) {
            return
        }
        flippedMemoryCardIndexes.add(memoryCardIndex)
        view?.flipMemoryCard(memoryCardIndex)
    }
}
