package dev.sanmer.authenticator.ui.ktx

import android.content.ClipData
import android.content.ClipDescription
import android.os.PersistableBundle
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.toClipEntry

fun ClipboardManager.setSensitiveText(
    content: String
) {
    val data = ClipData.newPlainText("plain text", content).apply {
        description.extras = PersistableBundle().apply {
            putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
        }
    }

    setClip(data.toClipEntry())
}