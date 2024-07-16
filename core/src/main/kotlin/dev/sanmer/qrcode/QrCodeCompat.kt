package dev.sanmer.qrcode

import android.graphics.Bitmap
import android.graphics.Color
import androidx.annotation.ColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.MultiFormatWriter
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.HybridBinarizer

@Suppress("NOTHING_TO_INLINE")
object QrCodeCompat {
    private val hints by lazy {
        hashMapOf<DecodeHintType, Any>().apply {
            put(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
            put(DecodeHintType.ALSO_INVERTED, true)
        }
    }

    private inline fun decode(source: LuminanceSource): String {
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        return MultiFormatReader().decode(bitmap, hints).text.trim()
    }

    fun decodeFromYuv(
        yuvData: ByteArray,
        dataWidth: Int,
        dataHeight: Int,
        width: Int,
        height: Int
    ) = decode(
        source = PlanarYUVLuminanceSource(
            yuvData,
            dataWidth,
            dataHeight,
            0,
            0,
            width,
            height,
            false
        )
    )

    private inline fun createBitmap(
        matrix: BitMatrix,
        @ColorInt foregroundColor: Int,
        @ColorInt backgroundColor: Int,
    ): Bitmap {
        val width = matrix.width
        val height = matrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (matrix[x, y]) foregroundColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    fun encodeToBitmap(
        contents: String,
        width: Int,
        height: Int,
        @ColorInt foregroundColor: Int = Color.WHITE,
        @ColorInt backgroundColor: Int = Color.BLACK,
    ) = createBitmap(
        matrix = MultiFormatWriter().encode(contents, BarcodeFormat.QR_CODE, width, height),
        foregroundColor = foregroundColor,
        backgroundColor = backgroundColor
    )
}