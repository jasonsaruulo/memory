package com.saruul.shafiq.memory.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.saruul.shafiq.memory.R
import com.squareup.picasso.Picasso
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
    lateinit var usedIndexes: MutableSet<Int>

    @BindView(R.id.main_permission_explanation) lateinit var permissionExplanation: TextView
    @BindView(R.id.main_game_board) lateinit var gameBoard: TableLayout

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
                permissionExplanation.setText(
                        R.string.main_read_external_storage_permission_explanation)
            } else {
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        readExternalStoragePermissionRequest)
            }
        } else {
            loadImages()
        }
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
                    loadImages()
                } else {
                    permissionExplanation.setText(
                            R.string.main_read_external_storage_permission_explanation)
                }
        }
    }

    private fun loadImages() {
        val cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media.DATA),
                null,
                null,
                null)
        cursor.moveToFirst()
        if (cursor.count > 9) {
            usedIndexes.clear()
            var i = 0
            while (i < gameBoard.childCount) {
                val row = gameBoard.getChildAt(i) as TableRow
                var j = 0
                while (j < row.childCount) {
                    var index = random.nextInt(cursor.count)
                    while (usedIndexes.contains(index)) {
                        index = random.nextInt(cursor.count)
                    }
                    usedIndexes.add(index)
                    cursor.moveToPosition(index)
                    val path = "file://" +
                            cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    val imageView = row.getChildAt(j) as ImageView
                    picasso.load(path)
                            .into(imageView)
                    j++
                }
                i++
            }
        }
        cursor.close()
    }
}
