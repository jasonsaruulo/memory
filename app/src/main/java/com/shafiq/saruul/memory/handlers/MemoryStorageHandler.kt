package com.shafiq.saruul.memory.handlers

import android.provider.MediaStore
import com.shafiq.saruul.memory.main.MainActivity
import javax.inject.Inject

class MemoryStorageHandler @Inject constructor(val mainActivity: MainActivity,
                                               val filePaths: MutableList<String>): StorageHandler {

    override fun getAllImagePaths(): MutableList<String> {
        filePaths.clear()
        val cursor = mainActivity.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
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
        return filePaths
    }
}