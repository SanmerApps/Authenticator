package dev.sanmer.authenticator.ui.screen.brand

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.plus
import dev.sanmer.authenticator.ui.ktx.surface
import dev.sanmer.brand.Brand

@Composable
fun BrandScreen(
    goBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopBar(
                onBack = goBack,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        LazyColumn(
            contentPadding = PaddingValues(15.dp) + contentPadding,
            verticalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            items(
                count = Brand.entries.size,
                key = { it }
            ) {
                BrandItem(
                    brand = Brand.entries[it]
                )
            }
        }
    }
}

@Composable
private fun BrandItem(
    brand: Brand,
    context: Context = LocalContext.current
) = Row(
    modifier = Modifier
        .fillMaxWidth()
        .surface(
            shape = MaterialTheme.shapes.large,
            backgroundColor = MaterialTheme.colorScheme.surface,
            border = CardDefaults.outlinedCardBorder(false)
        )
        .clickable(
            onClick = {
                context.startActivity(
                    Intent.parseUri("https://${brand.domains[0]}", Intent.URI_INTENT_SCHEME)
                )
            }
        )
        .padding(15.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(13.dp)
) {
    Image(
        painter = painterResource(brand.id),
        contentDescription = null,
        modifier = Modifier.size(45.dp)
    )

    Column(
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = brand.label,
            style = MaterialTheme.typography.titleMedium
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            brand.domains.forEach {
                DomainItem(it)
            }
        }
    }
}

@Composable
private fun DomainItem(
    domain: String
) = Row(
    horizontalArrangement = Arrangement.spacedBy(5.dp),
    verticalAlignment = Alignment.CenterVertically
) {
    Icon(
        painter = painterResource(R.drawable.globe_simple),
        contentDescription = null,
        modifier = Modifier.size(18.dp),
        tint = MaterialTheme.colorScheme.outline
    )

    Text(
        text = domain,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(R.string.setting_brand)) },
    navigationIcon = {
        IconButton(
            onClick = onBack
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)