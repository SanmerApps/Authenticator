package dev.sanmer.authenticator.ui.screens.settings.component

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.serializer.AuthJson

@Composable
fun ToolItem(
    onDismiss: () -> Unit,
    onEncode: () -> Unit,
    decryptFromJson: (Context, Uri, () -> Unit) -> Unit,
    decryptedToJson: (Context, Uri) -> Unit
) {
    val context = LocalContext.current
    val jsonExporter = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(AuthJson.MIME_TYPE),
        onResult = { if (it != null) decryptedToJson(context, it) }
    )
    val jsonImporter = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            if (it != null) decryptFromJson(context, it) {
                jsonExporter.launch(AuthJson.FILE_NAME)
            }
        }
    )

    SettingBottomSheet(
        onDismiss = onDismiss,
        title = stringResource(id = R.string.settings_tools),
    ) {
        SettingItem(
            icon = R.drawable.lock_open,
            title = stringResource(id = R.string.settings_decrypt),
            onClick = { jsonImporter.launch(AuthJson.MIME_TYPE) }
        )

        SettingItem(
            icon = R.drawable.a_b,
            title = stringResource(id = R.string.settings_encode_decode),
            onClick = {
                onEncode()
                onDismiss()
            }
        )
    }
}