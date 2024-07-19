package dev.sanmer.authenticator.ui.screens.trash.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ktx.hidden
import dev.sanmer.authenticator.model.auth.Auth
import dev.sanmer.authenticator.ui.component.SwipeContent
import dev.sanmer.authenticator.ui.ktx.surface

@Composable
fun AuthItem(
    auth: Auth,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) = SwipeContent(
    content = { release ->
        AuthItemButtons(
            onRestore = {
                release()
                onRestore()
            },
            onDelete = {
                release()
                onDelete()
            }
        )
    },
    surface = {
        AuthItemContent(
            auth = auth
        )
    }
)

@Composable
private fun AuthItemContent(
    auth: Auth
) = Row(
    modifier = Modifier
        .sizeIn(maxWidth = 450.dp)
        .fillMaxWidth()
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder()
        )
        .padding(all = 15.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(10.dp)
) {
    val hiddenSecret by remember {
        derivedStateOf { auth.secret.hidden() }
    }

    Column(
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = hiddenSecret,
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = auth.displayName,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun AuthItemButtons(
    onRestore: () -> Unit,
    onDelete: () -> Unit
) = Row(
    modifier = Modifier.padding(horizontal = 10.dp),
    horizontalArrangement = Arrangement.spacedBy(5.dp)
) {
    FilledTonalIconButton(
        onClick = onRestore
    ) {
        Icon(
            painter = painterResource(id = R.drawable.restore),
            contentDescription = null
        )
    }

    FilledTonalIconButton(
        onClick = onDelete,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Icon(
            painter = painterResource(id = R.drawable.trash_x),
            contentDescription = null
        )
    }
}