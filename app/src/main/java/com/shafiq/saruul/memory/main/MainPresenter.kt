package com.shafiq.saruul.memory.main

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.shafiq.saruul.memory.handlers.PermissionHandler
import com.shafiq.saruul.memory.handlers.StorageHandler
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.util.*
import javax.inject.Inject

class MainPresenter @Inject constructor(val random: Random,
                                        val picasso: Picasso,
                                        val filePaths: MutableList<String>,
                                        val picassoTargets: MutableMap<String, Target>,
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
                loadImage()
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

    private fun loadImage() {
        if (filePaths.isEmpty()) {
            // TODO: Show error message
            showGameBoard()
            return
        }
        val filePath = unusedFilePath()
        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {
                // TODO: Handle failure better instead of continuously loading images
                picassoTargets.remove(filePath)
                loadImage()
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                picassoTargets.remove(filePath)
                imageLoaded(bitmap)
            }
        }
        picassoTargets.put(filePath, target)
        picasso.load(filePath).into(target)
    }

    private fun imageLoaded(bitmap: Bitmap?) {
        numberOfImagesLoaded++
        view?.setImage(unusedMemoryCardIndex(), bitmap)
        if (numberOfImagesLoaded == numberOfMemoryCards) {
            showGameBoard()
            // TODO: Start the game
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

    override fun memoryCardClicked(memoryCardIndex: Int) {
        view?.flipMemoryCard(memoryCardIndex)
    }
}
