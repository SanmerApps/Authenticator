package dev.sanmer.authenticator.ui.screens.trash.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.database.entity.TotpEntity
import dev.sanmer.authenticator.ktx.hidden
import dev.sanmer.authenticator.ui.component.LabelText
import dev.sanmer.authenticator.ui.component.SwipeContent
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.logo.Logo
import kotlin.time.DurationUnit

@Composable
fun AuthItem(
    entity: TotpEntity,
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
            entity = entity
        )
    }
)

@Composable
private fun AuthItemContent(
    entity: TotpEntity
) = Row(
    modifier = Modifier
        .sizeIn(maxWidth = 450.dp)
        .fillMaxWidth()
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .padding(all = 15.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp)
) {
    val logo by remember(entity.issuer) { derivedStateOf { Logo.getOrDefault(entity.issuer) } }
    val hiddenSecret by remember { derivedStateOf { entity.secret.hidden() } }
    val lifetimeString by remember {
        derivedStateOf { entity.lifetime.toString(unit = DurationUnit.HOURS, decimals = 2) }
    }

    Image(
        painter = painterResource(id = logo.res),
        contentDescription = null,
        modifier = Modifier.size(40.dp),
        colorFilter = if (logo.refillable) {
            ColorFilter.tint(MaterialTheme.colorScheme.primary)
        } else null
    )

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = hiddenSecret,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontFamily = FontFamily.Monospace
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            LabelText(text = lifetimeString)
        }

        Text(
            text = entity.displayName,
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