package com.shafiq.saruul.memory.handlers

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import com.shafiq.saruul.memory.handlers.PermissionHandler.Companion.readExternalStoragePermissionRequest
import com.shafiq.saruul.memory.main.MainActivity
import javax.inject.Inject

class MemoryPermissionHandler @Inject constructor(val mainActivity: MainActivity):
        PermissionHandler {

    override fun permissionReadExternalStorageGranted(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
                mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    override fun shouldShowRequestPermissionRationale(): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
                mainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun requestReadExternalStoragePermission() {
        ActivityCompat.requestPermissions(mainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                readExternalStoragePermissionRequest)
    }
}
