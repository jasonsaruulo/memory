package com.shafiq.saruul.memory.handlers

interface PermissionHandler {

    companion object {
        val readExternalStoragePermissionRequest = 100
    }

    fun permissionReadExternalStorageGranted(): Boolean

    fun shouldShowRequestPermissionRationale(): Boolean

    fun requestReadExternalStoragePermission()
}