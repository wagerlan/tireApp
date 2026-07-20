package com.example.aibutler.platform

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.aibutler.domain.WatchTask

/**
 * 只负责「合法拉起官方 App」，不做模拟点击。
 */
class OfficialAppLauncher(private val context: Context) {

    fun launch(task: WatchTask): Boolean {
        // 1) Deep Link 优先
        task.deepLink?.let { link ->
            if (openUri(link)) return true
        }
        // 2) 包名打开
        task.packageName?.let { pkg ->
            val launch = context.packageManager.getLaunchIntentForPackage(pkg)
            if (launch != null) {
                launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launch)
                return true
            }
        }
        return false
    }

    private fun openUri(link: String): Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: ActivityNotFoundException) {
            false
        }
    }

    companion object {
        /** 示例包名，实际以设备安装为准，且会随版本变化 */
        const val PACKAGE_DAMAI = "cn.damai"
    }
}
