package dev.sanmer.authenticator.ui.screen.trash.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.Const.DATETIME_DISPLAY
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.database.model.Auth
import dev.sanmer.authenticator.ui.component.LabelText
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.brand.Brand
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime

@Composable
fun AuthItem(
    auth: Auth,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) = Row(
    modifier = modifier
        .fillMaxWidth()
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick
        )
        .padding(15.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(13.dp)
) {
    val brand by remember(auth.id) {
        derivedStateOf { Brand.valueOfOrNull(auth.issuer) }
    }
    val trashedAt by remember(auth.id) {
        derivedStateOf {
            auth.trashedAt
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .format(DATETIME_DISPLAY)
        }
    }

    Image(
        painter = painterResource(brand?.id ?: R.drawable.fingerprint_simple),
        contentDescription = null,
        modifier = Modifier.size(45.dp),
        colorFilter = if (brand != null) null else
            ColorFilter.tint(MaterialTheme.colorScheme.primary)
    )

    Column(
        modifier = Modifier.weight(1f)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (auth.name.isNotEmpty()) Text(
                text = auth.name,
                style = MaterialTheme.typography.titleMedium
            )

            if (brand == null && auth.issuer.isNotEmpty()) LabelText(
                text = auth.issuer
            )
        }

        Text(
            text = stringResource(R.string.trashed_at, trashedAt),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }

    if (isSelected) Icon(
        painter = painterResource(R.drawable.check_circle_fill),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(30.dp)
    )
}