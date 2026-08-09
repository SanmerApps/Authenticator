package dev.sanmer.authenticator.ui.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun Loading(
    modifier: Modifier = Modifier
) = Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
) {
    CircularProgressIndicator(
        modifier = Modifier.size(48.dp),
        strokeWidth = 4.dp
    )
}

@Composable
fun Finished(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge
) = Box(
    modifier = modifier,
    contentAlignment = Alignment.Center
) {
    CompositionLocalProvider(
        LocalContentColor provides color,
    ) {
        ProvideTextStyle(textStyle) {
            label()
        }
    }
}

@Composable
fun Finished(
    label: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge
) = Finished(
    label = { Text(text = label) },
    modifier = modifier,
    color = color,
    textStyle = textStyle
)

@Composable
fun Finished(
    @StringRes label: Int,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge
) = Finished(
    label = stringResource(label),
    modifier = modifier,
    color = color,
    textStyle = textStyle
)