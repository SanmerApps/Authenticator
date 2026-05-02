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
import dev.sanmer.authenticator.model.serializer.AuthUri

@Composable
fun DatabaseItem(
    onDismiss: () -> Unit,
    isEmpty: Boolean,
    importJson: (Context, Uri) -> Unit,
    importUri: (Context, Uri) -> Unit,
    encrypt: (Context, () -> Unit) -> Unit,
    exportJson: (Context, Uri) -> Unit,
    exportUri: (Context, Uri) -> Unit,
) {
    val context = LocalContext.current
    val jsonImporter = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { if (it != null) importJson(context, it) }
    )
    val jsonExporter = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(AuthJson.MIME_TYPE),
        onResult = { if (it != null) exportJson(context, it) }
    )
    val uriImporter = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { if (it != null) importUri(context, it) }
    )
    val uriExporter = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(AuthUri.MIME_TYPE),
        onResult = { if (it != null) exportUri(context, it) }
    )

    SettingBottomSheet(
        onDismiss = onDismiss,
        title = stringResource(id = R.string.settings_import_export)
    ) {
        SettingItem(
            icon = R.drawable.database_import,
            title = stringResource(id = R.string.settings_import_json),
            onClick = { jsonImporter.launch(AuthJson.MIME_TYPE) }
        )

        if (!isEmpty) SettingItem(
            icon = R.drawable.database_export,
            title = stringResource(id = R.string.settings_export_json),
            onClick = { encrypt(context) { jsonExporter.launch(AuthJson.FILE_NAME) } }
        )

        SettingItem(
            icon = R.drawable.file_export,
            title = stringResource(id = R.string.settings_import_txt),
            onClick = { uriImporter.launch(AuthUri.MIME_TYPE) }
        )

        if (!isEmpty) SettingItem(
            icon = R.drawable.file_import,
            title = stringResource(id = R.string.settings_export_txt),
            onClick = { uriExporter.launch(AuthUri.FILE_NAME) }
        )
    }
}