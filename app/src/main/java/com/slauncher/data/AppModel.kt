package com.slauncher.data

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.Settings
import android.widget.Toast

/**
 * Represents an installed launchable application.
 */
data class AppModel(
    val packageName: String,
    val activityName: String,
    val label: String,
    val icon: Bitmap? = null
) {
    /**
     * Launches the application.
     */
    fun launch(context: Context) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)?.apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            if (intent != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Cannot open $label", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error launching $label", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens system App Info page for this package.
     */
    fun openAppInfo(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot open settings for $label", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Triggers the system uninstall prompt.
     */
    fun requestUninstall(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_DELETE).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Cannot uninstall $label", Toast.LENGTH_SHORT).show()
        }
    }
}
