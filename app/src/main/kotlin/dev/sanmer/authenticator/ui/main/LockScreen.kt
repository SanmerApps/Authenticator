package dev.sanmer.authenticator.ui.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import dev.sanmer.authenticator.R
import dev.sanmer.authenticator.ktx.finishActivity
import dev.sanmer.authenticator.ui.AuthorizeActivity
import dev.sanmer.authenticator.viewmodel.MainViewModel

@Composable
fun LockScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    DisposableEffect(true) {
        AuthorizeActivity.auth(context) {
            if (it) viewModel.setUnlocked()
        }
        onDispose {}
    }

    BackHandler {
        context.finishActivity()
    }

    Box(
        modifier = Modifier
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = {
                    AuthorizeActivity.auth(context) {
                        if (it) viewModel.setUnlocked()
                    }
                }
            )
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.lock),
            contentDescription = null,
            modifier = Modifier.size(50.dp),
            tint = MaterialTheme.colorScheme.outline
        )
    }
}