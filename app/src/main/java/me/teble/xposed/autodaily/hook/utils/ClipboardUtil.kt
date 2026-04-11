package me.teble.xposed.autodaily.hook.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import me.teble.xposed.autodaily.config.NAME
import me.teble.xposed.autodaily.hook.base.hostContext
import me.teble.xposed.autodaily.utils.LogUtil

object ClipboardUtil {

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private fun copyToClipboard(context: Context, text: CharSequence, label: String = NAME) {
        runCatching {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        }.onFailure {
            LogUtil.e(it, "copy to clipboard")
        }
    }

    @JvmOverloads
    fun copy(text: CharSequence, label: String = NAME) {
        copyToClipboard(hostContext, text, label)
    }

    @JvmOverloads
    fun copy(context: Context, text: CharSequence, label: String = NAME) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            copyToClipboard(context, text, label)
        } else {
            handler.post { copyToClipboard(context, text, label) }
        }
    }
}
