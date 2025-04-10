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
import dev.sanmer.authenticator.model.serializer.AuthTxt
import dev.sanmer.authenticator.viewmodel.SettingsViewModel.FileType

@Composable
fun DatabaseItem(
    onDismiss: () -> Unit,
    isEmpty: Boolean,
    prepare: (FileType, Context, () -> Unit) -> Unit,
    importFrom: (FileType, Context, Uri) -> Unit,
    exportTo: (FileType, Context, Uri) -> Unit,
) {
    val context = LocalContext.current
    val jsonImport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { if (it != null) importFrom(FileType.Json, context, it) }
    )
    val jsonExport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(AuthJson.MIME_TYPE),
        onResult = { if (it != null) exportTo(FileType.Json, context, it) }
    )
    val txtImport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { if (it != null) importFrom(FileType.Txt, context, it) }
    )
    val txtExport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(AuthTxt.MIME_TYPE),
        onResult = { if (it != null) exportTo(FileType.Txt, context, it) }
    )

    SettingBottomSheet(
        onDismiss = onDismiss,
        title = stringResource(id = R.string.settings_import_export)
    ) {
        SettingItem(
            icon = R.drawable.database_import,
            title = stringResource(id = R.string.settings_import_json),
            onClick = { jsonImport.launch(AuthJson.MIME_TYPE) }
        )

        if (!isEmpty) SettingItem(
            icon = R.drawable.database_export,
            title = stringResource(id = R.string.settings_export_json),
            onClick = { prepare(FileType.Json, context) { jsonExport.launch(AuthJson.FILE_NAME) } }
        )

        SettingItem(
            icon = R.drawable.file_export,
            title = stringResource(id = R.string.settings_import_txt),
            onClick = { txtImport.launch(AuthTxt.MIME_TYPE) }
        )

        if (!isEmpty) SettingItem(
            icon = R.drawable.file_import,
            title = stringResource(id = R.string.settings_export_txt),
            onClick = { prepare(FileType.Txt, context) { txtExport.launch(AuthTxt.FILE_NAME) } }
        )
    }
}