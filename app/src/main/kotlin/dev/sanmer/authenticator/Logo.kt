package dev.sanmer.authenticator

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val Aliyun = ImageVector.Builder(
    name = "Aliyun",
    defaultWidth = 24.dp,
    defaultHeight = 24.dp,
    viewportWidth = 1608f,
    viewportHeight = 1024f
).apply {
    // 中间横条
    path(
        fill = SolidColor(Color(0xFFFF6A00))
    ) {
        moveTo(537.6f, 445.44f)
        lineTo(1075.2f, 445.44f)
        lineTo(1075.2f, 568.32f)
        lineTo(537.6f, 568.32f)
        close()
    }

    // 阿里云主体图形
    path(
        fill = SolidColor(Color(0xFFFF6A00))
    ) {
        moveTo(1341.44f, 5.12f)
        lineTo(988.16f, 5.12f)
        lineTo(1075.2f, 128f)
        lineTo(1331.2f, 209.92f)

        curveTo(
            1377.28f, 225.28f,
            1408f, 271.36f,
            1408f, 317.44f
        )

        lineTo(1408f, 706.56f)

        curveTo(
            1408f, 752.64f,
            1377.28f, 798.72f,
            1331.2f, 814.08f
        )

        lineTo(1075.2f, 896f)
        lineTo(988.16f, 1018.88f)
        lineTo(1341.44f, 1018.88f)

        curveTo(
            1489.92f, 1018.88f,
            1607.68f, 901.12f,
            1607.68f, 752.64f
        )

        lineTo(1607.68f, 276.48f)

        curveTo(
            1607.68f, 128f,
            1489.92f, 5.12f,
            1341.44f, 5.12f
        )

        moveTo(276.48f, 814.08f)
        curveTo(
            230.4f, 798.72f,
            199.68f, 752.64f,
            199.68f, 706.56f
        )

        lineTo(199.68f, 317.44f)

        curveTo(
            199.68f, 271.36f,
            230.4f, 225.28f,
            276.48f, 209.92f
        )

        lineTo(532.48f, 128f)
        lineTo(619.52f, 5.12f)
        lineTo(266.24f, 5.12f)

        curveTo(
            117.76f, 5.12f,
            0f, 128f,
            0f, 276.48f
        )

        lineTo(0f, 747.52f)

        curveTo(
            0f, 896f,
            117.76f, 1013.76f,
            266.24f, 1013.76f
        )

        lineTo(619.52f, 1013.76f)
        lineTo(532.48f, 890.88f)
        lineTo(276.48f, 814.08f)

        close()
    }
}.build()

@Preview
@Composable
private fun Aliyun() {
    Icon(
        imageVector = Aliyun,
        contentDescription = null
    )
}