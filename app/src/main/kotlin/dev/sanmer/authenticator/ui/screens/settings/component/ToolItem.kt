package dev.sanmer.authenticator.ui.screens.settings.component

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.model.serializer.AuthJson
import dev.sanmer.authenticator.ui.ktx.bottom
import dev.sanmer.authenticator.ui.ktx.navigateSingleTopTo
import dev.sanmer.authenticator.ui.main.Screen

@Composable
fun ToolItem(
    onDismiss: () -> Unit,
    navController: NavController,
    decryptFromJson: (Context, Uri, () -> Unit) -> Unit,
    decryptedToJson: (Context, Uri) -> Unit
) {
    val context = LocalContext.current
    val jsonDecrypt = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(AuthJson.MIME_TYPE),
        onResult = { if (it != null) decryptedToJson(context, it) }
    )
    val jsonEncrypt = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            if (it != null) decryptFromJson(context, it) {
                jsonDecrypt.launch(AuthJson.FILE_NAME)
            }
        }
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large.bottom(0.dp)
    ) {
        Text(
            text = stringResource(id = R.string.settings_tools),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Column(
            modifier = Modifier.padding(all = 15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            SettingItem(
                icon = R.drawable.lock_open,
                title = stringResource(id = R.string.settings_decrypt),
                onClick = { jsonEncrypt.launch(AuthJson.MIME_TYPE) }
            )

            SettingItem(
                icon = R.drawable.a_b,
                title = stringResource(id = R.string.settings_encode_decode),
                onClick = {
                    navController.navigateSingleTopTo(Screen.Encode())
                    onDismiss()
                }
            )
        }
    }
}