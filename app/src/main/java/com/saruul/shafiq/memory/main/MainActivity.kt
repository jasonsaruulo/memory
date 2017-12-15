package com.saruul.shafiq.memory.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.saruul.shafiq.memory.R
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.android.AndroidInjection
import java.util.Random
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private val readExternalStoragePermissionRequest = 100
    @Inject
    lateinit var random: Random
    @Inject
    lateinit var picasso: Picasso
    @Inject
    lateinit var filePaths: MutableList<String>
    private val numberOfMemoryCards = 9
    private var numberOfImagesLoaded = 0

    @BindView(R.id.main_progress_bar) lateinit var progressBar: View
    @BindView(R.id.main_permission_explanation) lateinit var permissionExplanation: View
    @BindView(R.id.main_game_board) lateinit var gameBoard: TableLayout
    @BindView(R.id.main_new_game) lateinit var newGame: View

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
    }

    @OnClick(R.id.main_new_game)
    fun requestReadExternalStoragePermission() {
        val permissionReadExternalStorage = ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)
        if (permissionReadExternalStorage != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionExplanation()
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        readExternalStoragePermissionRequest)
            }
        } else {
            loadFilePaths()
        }
    }

    private fun showPermissionExplanation() {
        progressBar.visibility = View.INVISIBLE
        permissionExplanation.visibility = View.VISIBLE
        gameBoard.visibility = View.INVISIBLE
        newGame.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        permissionExplanation.visibility = View.INVISIBLE
        gameBoard.visibility = View.INVISIBLE
        newGame.visibility = View.INVISIBLE
    }

    private fun showGameBoard() {
        progressBar.visibility = View.INVISIBLE
        permissionExplanation.visibility = View.INVISIBLE
        gameBoard.visibility = View.VISIBLE
        newGame.visibility = View.VISIBLE
    }

    override fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) {
            return
        }
        when (requestCode) {
            readExternalStoragePermissionRequest ->
                if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    loadFilePaths()
                } else {
                    showPermissionExplanation()
                }
        }
    }

    private fun loadFilePaths() {
        showProgressBar()
        if (filePaths.isEmpty() || filePaths.size < numberOfMemoryCards) {
            val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Images.Media.DATA),
                    null,
                    null,
                    null)
            cursor.moveToFirst()
            do {
                val path = "file://" +
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                filePaths.add(path)
            } while (cursor.moveToNext())
            cursor.close()
        }
        loadImages()
    }

    private fun loadImages() {
        if (filePaths.size >= numberOfMemoryCards) {
            var i = 0
            while (i < gameBoard.childCount) {
                val row = gameBoard.getChildAt(i) as TableRow
                var j = 0
                while (j < row.childCount) {
                    val memoryCardView = row.getChildAt(j) as MemoryCardView
                    loadImage(memoryCardView)
                    j++
                }
                i++
            }
        } else {
            // TODO: Show error message
        }
    }

    private fun randomIndex(): Int {
        return random.nextInt(filePaths.size)
    }

    private fun loadImage(memoryCardView: MemoryCardView) {
        memoryCardView.placeholder.alpha = 1f
        memoryCardView.content.alpha = 0f
        if (filePaths.isEmpty()) {
            // TODO: Show error message
            showGameBoard()
            return
        }
        val index = randomIndex()
        val path = filePaths[index]
        filePaths.removeAt(index)
        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            }

            override fun onBitmapFailed(errorDrawable: Drawable?) {
                // TODO: Handle failure better instead of continuously loading images
                loadImage(memoryCardView)
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                imageLoaded(memoryCardView, bitmap)
            }
        }
        memoryCardView.tag = target
        picasso.load(path).into(target)
    }

    private fun imageLoaded(memoryCardView: MemoryCardView, bitmap: Bitmap?) {
        numberOfImagesLoaded++
        memoryCardView.content.setImageBitmap(bitmap)
        if (numberOfImagesLoaded == numberOfMemoryCards) {
            showGameBoard()
            // TODO: Start the game
        }
    }
}
