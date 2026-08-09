package dev.sanmer.authenticator.ui.screen.brand

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.plus
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
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            contentPadding = PaddingValues(15.dp) + contentPadding,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            items(
                count = Brand.entries.size
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
    brand: Brand
) = Column(
    modifier = Modifier
        .border(
            border = CardDefaults.outlinedCardBorder(false),
            shape = MaterialTheme.shapes.large
        )
        .size(100.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
) {
    Image(
        painter = painterResource(brand.id),
        contentDescription = null,
        modifier = Modifier.size(45.dp)
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = brand.name,
        style = MaterialTheme.typography.labelLarge,
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