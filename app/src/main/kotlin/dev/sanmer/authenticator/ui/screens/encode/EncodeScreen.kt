package dev.sanmer.authenticator.ui.screens.encode

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.viewmodel.EncodeViewModel

@Composable
fun EncodeScreen(
    viewModel: EncodeViewModel = hiltViewModel(),
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val maxWidth by remember {
        derivedStateOf { configuration.smallestScreenWidthDp * 0.8f }
    }

    Scaffold(
        topBar = {
            TopBar(
                navController = navController,
                scrollBehavior = scrollBehavior
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .imePadding()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(contentPadding)
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            SingleChoiceSegmentedButtonRow {
                EncodeViewModel.Type.entries.forEachIndexed { index, type ->
                    val selected by remember { derivedStateOf { viewModel.type == type } }
                    SegmentedButton(
                        selected = selected,
                        onClick = { viewModel.updateType(type) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = EncodeViewModel.Type.entries.size
                        ),
                        icon = {
                            SegmentedIcon(active = selected)
                        }
                    ) {
                        Text(text = type.name)
                    }
                }
            }

            var decoded by remember(viewModel.decoded) {
                mutableStateOf(
                    TextFieldValue(
                        text = viewModel.decoded,
                        selection = TextRange(viewModel.decoded.length)
                    )
                )
            }
            OutlinedTextField(
                value = decoded,
                onValueChange = {
                    decoded = it
                    viewModel.updateDecoded(it.text)
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(width = maxWidth.dp, height = 180.dp),
                isError = viewModel.isError(EncodeViewModel.Error.Encode),
                placeholder = { Text(text = stringResource(id = R.string.encode_decoded)) }
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ButtonItem(
                    onClick = viewModel::encode,
                    icon = R.drawable.arrow_bar_down
                )

                Spacer(modifier = Modifier.width(10.dp))

                ButtonItem(
                    onClick = viewModel::decode,
                    icon = R.drawable.arrow_bar_up
                )
            }

            var encoded by remember(viewModel.encoded) {
                mutableStateOf(
                    TextFieldValue(
                        text = viewModel.encoded,
                        selection = TextRange(viewModel.encoded.length)
                    )
                )
            }
            OutlinedTextField(
                value = encoded,
                onValueChange = {
                    encoded = it
                    viewModel.updateEncoded(it.text)
                },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(width = maxWidth.dp, height = 180.dp),
                isError = viewModel.isError(EncodeViewModel.Error.Decode),
                placeholder = { Text(text = stringResource(id = R.string.encode_encoded)) }
            )
        }
    }
}

@Composable
private fun SegmentedIcon(
    active: Boolean
) = SegmentedButtonDefaults.Icon(
    active = active,
    activeContent = {
        Icon(
            painter = painterResource(id = R.drawable.check),
            contentDescription = null,
            modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
        )
    }
)

@Composable
private fun ButtonItem(
    onClick: () -> Unit,
    @DrawableRes icon: Int
) = IconButton(
    onClick = onClick
) {
    Icon(
        painter = painterResource(id = icon),
        contentDescription = null
    )
}

@Composable
private fun TopBar(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = { Text(text = stringResource(id = R.string.settings_encode_decode)) },
    navigationIcon = {
        IconButton(
            onClick = { navController.navigateUp() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    scrollBehavior = scrollBehavior
)