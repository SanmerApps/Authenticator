package dev.sanmer.authenticator.ui.ktx

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.PI
import kotlin.math.tan

fun CornerBasedShape.bottom(size: Dp) =
    copy(bottomStart = CornerSize(size), bottomEnd = CornerSize(size))

class TrapezoidShape(
    private val bottomAngle: Float,
    private val reversed: Boolean = false
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val offset = (size.height / tan(bottomAngle * PI / 180)).toFloat()

        val path = Path().apply {
            if (reversed) {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width - offset, size.height)
                lineTo(offset, size.height)
                close()
            } else {
                moveTo(size.width, size.height)
                lineTo(0f, size.height)
                lineTo(offset, 0f)
                lineTo(size.width - offset, 0f)
            }
        }

        return Outline.Generic(path)
    }
}