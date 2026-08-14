package dev.sanmer.authenticator.ui.ktx

import android.content.ClipData
import android.content.ClipDescription
import android.os.PersistableBundle
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.toClipEntry
import dev.sanmer.authenticator.compat.BuildCompat

suspend fun Clipboard.setSensitiveText(content: String) {
    val data = ClipData.newPlainText("plain text", content).apply {
        description.extras = PersistableBundle().apply {
            if (BuildCompat.atLeastT) putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
        }
    }
    setClipEntry(data.toClipEntry())
}