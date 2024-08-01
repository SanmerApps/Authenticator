package dev.sanmer.authenticator.ui.screens.settings.component

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.serializer.AuthJson
import dev.sanmer.authenticator.ui.ktx.bottom

@Composable
fun DatabaseItem(
    onDismiss: () -> Unit,
    encrypt: (Context, () -> Unit) -> Unit,
    importFromJson: (Context, Uri) -> Unit,
    exportToJson: (Context, Uri) -> Unit
) {
    val context = LocalContext.current
    val jsonImport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { if (it != null) importFromJson(context, it) }
    )
    val jsonExport = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(AuthJson.MIME_TYPE),
        onResult = { if (it != null) exportToJson(context, it) }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large.bottom(0.dp),
        windowInsets = WindowInsets(0.dp)
    ) {
        val contentPadding = WindowInsets.navigationBars.asPaddingValues()

        Text(
            text = stringResource(id = R.string.settings_import_export),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier
                .padding(all = 15.dp)
                .padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            SettingItem(
                icon = R.drawable.database_import,
                title = stringResource(id = R.string.settings_import),
                onClick = { jsonImport.launch(AuthJson.MIME_TYPE) }
            )

            SettingItem(
                icon = R.drawable.database_export,
                title = stringResource(id = R.string.settings_export),
                onClick = { encrypt(context) { jsonExport.launch(AuthJson.FILE_NAME) } }
            )
        }
    }
}