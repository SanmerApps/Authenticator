package dev.sanmer.authenticator.ui.screen.edit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ui.ktx.plus
import dev.sanmer.authenticator.ui.screen.edit.EditViewModel.BottomSheet
import dev.sanmer.authenticator.ui.screen.edit.component.DigitsItem
import dev.sanmer.authenticator.ui.screen.edit.component.IssuerItem
import dev.sanmer.authenticator.ui.screen.edit.component.NameItem
import dev.sanmer.authenticator.ui.screen.edit.component.PreviewBottomSheet
import dev.sanmer.authenticator.ui.screen.edit.component.QrcodeBottomSheet
import dev.sanmer.authenticator.ui.screen.edit.component.SecretItem
import dev.sanmer.authenticator.ui.screen.edit.component.TypeItem
import dev.sanmer.authenticator.ui.screen.scan.ScanScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun EditScreen(
    viewModel: EditViewModel,
    goBack: () -> Unit
) = AnimatedContent(
    targetState = viewModel.bottomSheet,
    transitionSpec = {
        fadeIn(
            animationSpec = tween(500)
        ) togetherWith fadeOut(
            animationSpec = tween(500)
        )
    },
    contentKey = { it == BottomSheet.Scan }
) {
    when (it) {
        BottomSheet.Scan -> ScanScreen(
            viewModel = koinViewModel { parametersOf(viewModel::fromScan) },
            goBack = { viewModel.bottomSheet = BottomSheet.None }
        )

        else -> EditScreen(
            viewModel = viewModel,
            goBack = goBack,
            onScan = { viewModel.bottomSheet = BottomSheet.Scan }
        )
    }
}

@Composable
fun EditScreen(
    viewModel: EditViewModel,
    goBack: () -> Unit,
    onScan: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    when (val bs = viewModel.bottomSheet) {
        BottomSheet.None, BottomSheet.Scan -> {}
        is BottomSheet.Preview -> PreviewBottomSheet(
            onClose = { viewModel.bottomSheet = BottomSheet.None },
            preview = bs.preview,
            onSave = { viewModel.save(it, goBack) }
        )

        is BottomSheet.Qrcode -> QrcodeBottomSheet(
            onClose = { viewModel.bottomSheet = BottomSheet.None },
            qrcode = bs.qrcode
        )
    }

    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            TopBar(
                isEdit = viewModel.isEdit,
                isTrashed = viewModel.isTrashed,
                onBack = goBack,
                onTrash = viewModel::trash,
                onDelete = { viewModel.delete(goBack) },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ActionButton(
                isTrashed = viewModel.isTrashed,
                onRestore = viewModel::restore,
                onSave = { viewModel.preview() },
                visible = viewModel.input.isNotEmpty
            )
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(horizontal = 5.dp, vertical = 15.dp) + contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NameItem(
                name = viewModel.input.name,
                enabled = !viewModel.isTrashed
            )

            IssuerItem(
                issuer = viewModel.input.issuer,
                brand = viewModel.brand,
                onMatchesBrand = viewModel::matchesBrand,
                enabled = !viewModel.isTrashed
            )

            SecretItem(
                secret = viewModel.input.secret,
                hidden = viewModel.input.hidden,
                isEdit = viewModel.isEdit,
                enabled = !viewModel.isTrashed
            )

            TypeItem(
                type = viewModel.input.type,
                hash = viewModel.input.hash,
                enabled = !viewModel.isTrashed
            )

            DigitsItem(
                digits = viewModel.input.digits,
                period = viewModel.input.period,
                enabled = !viewModel.isTrashed
            )

            val keyboardController = LocalSoftwareKeyboardController.current
            val density = LocalDensity.current
            val color = MaterialTheme.colorScheme.onSurface
            FilledTonalIconButton(
                onClick = {
                    keyboardController?.hide()
                    if (viewModel.isEdit) {
                        viewModel.qrcode(density, color)
                    } else {
                        onScan()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.qrcode),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    isEdit: Boolean,
    isTrashed: Boolean,
    onBack: () -> Unit,
    onTrash: () -> Unit,
    onDelete: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) = TopAppBar(
    title = {
        Text(
            text = stringResource(
                if (isEdit) R.string.token_edit_title
                else R.string.token_add_title
            )
        )
    },
    navigationIcon = {
        val keyboardController = LocalSoftwareKeyboardController.current
        IconButton(
            onClick = {
                keyboardController?.hide()
                onBack()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.arrow_left),
                contentDescription = null
            )
        }
    },
    actions = {
        if (isEdit) AnimatedContent(
            targetState = isTrashed,
            transitionSpec = {
                fadeIn() + scaleIn(
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                ) togetherWith scaleOut(
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                ) + fadeOut()
            }
        ) { isTrashed ->
            IconButton(
                onClick = {
                    when {
                        transition.isRunning -> {}
                        else -> if (isTrashed) onDelete() else onTrash()
                    }
                }
            ) {
                Icon(
                    painter = painterResource(
                        if (isTrashed) R.drawable.trash_x
                        else R.drawable.trash
                    ),
                    contentDescription = null
                )
            }
        }
    },
    scrollBehavior = scrollBehavior
)

@Composable
private fun ActionButton(
    isTrashed: Boolean,
    onRestore: () -> Unit,
    onSave: () -> Unit,
    visible: Boolean = true
) = AnimatedVisibility(
    visible = visible,
    enter = fadeIn() + scaleIn(),
    exit = scaleOut() + fadeOut()
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    FloatingActionButton(
        onClick = {
            keyboardController?.hide()
            if (isTrashed) onRestore() else onSave()
        },
    ) {
        Icon(
            painter = painterResource(
                if (isTrashed) R.drawable.restore
                else R.drawable.device_floppy
            ),
            contentDescription = null
        )
    }
}