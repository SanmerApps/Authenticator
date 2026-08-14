package dev.sanmer.authenticator.ui.screen.export.component

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedSecureTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.authenticator.ui.screen.export.ExportViewModel

@Composable
fun ButtonsItem(
    password: TextFieldState,
    hidden: MutableState<Boolean>,
    type: MutableState<ExportViewModel.Input.Type>,
    onImport: (Context, Uri) -> Unit,
    onExport: (Context, Uri) -> Unit,
    isNotEmpty: Boolean,
    modifier: Modifier = Modifier
) = Column(
    modifier = modifier
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .padding(15.dp),
    verticalArrangement = Arrangement.spacedBy(15.dp)
) {
    val isJson by remember(type) {
        derivedStateOf {
            type.value == ExportViewModel.Input.Type.Json
        }
    }

    OutlinedSecureTextField(
        state = password,
        enabled = isJson,
        placeholder = { Text(text = stringResource(R.string.setting_password)) },
        trailingIcon = {
            IconButton(
                onClick = { hidden.value = !hidden.value },
                enabled = isJson
            ) {
                Icon(
                    painter = painterResource(
                        if (hidden.value) R.drawable.sparkles_2
                        else R.drawable.sparkles_2_off
                    ),
                    contentDescription = null
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        shape = MaterialTheme.shapes.medium,
        textObfuscationMode = with(TextObfuscationMode) { if (hidden.value) Hidden else Visible },
        modifier = Modifier.fillMaxWidth()
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        SingleChoiceSegmentedButtonRow {
            ExportViewModel.Input.Type.entries.forEachIndexed { index, value ->
                SegmentedButton(
                    selected = type.value == value,
                    onClick = { type.value = value },
                    shape = SegmentedButtonDefaults.itemShape(
                        index,
                        ExportViewModel.Input.Type.entries.size
                    ),
                    label = { Text(text = value.name) }
                )
            }
        }

        val context = LocalContext.current
        val importer = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { if (it != null) onImport(context, it) }
        )
        FilledTonalIconButton(
            onClick = { importer.launch(type.value.mimeType) },
            enabled = !isNotEmpty
        ) {
            Icon(
                painter = painterResource(R.drawable.download),
                contentDescription = null
            )
        }

        val exporter = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument(type.value.mimeType),
            onResult = { if (it != null) onExport(context, it) }
        )
        FilledTonalIconButton(
            onClick = { exporter.launch("auth") },
            enabled = isNotEmpty
        ) {
            Icon(
                painter = painterResource(R.drawable.upload),
                contentDescription = null
            )
        }
    }
}