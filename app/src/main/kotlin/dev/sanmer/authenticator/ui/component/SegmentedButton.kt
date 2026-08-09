package dev.sanmer.authenticator.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import dev.sanmer.authenticator.R

@Composable
fun SegmentedButtonDefaults.Check(
    active: Boolean
) = Icon(
    active = active,
    activeContent = {
        androidx.compose.material3.Icon(
            painter = painterResource(R.drawable.check),
            contentDescription = null,
            modifier = Modifier.size(IconSize)
        )
    }
)