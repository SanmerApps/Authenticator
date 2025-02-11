package dev.sanmer.qrcode

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.annotation.ColorInt
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.EncodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.BitMatrix
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.InputStream

object QRCode {
    private val decodeHint by lazy {
        hashMapOf<DecodeHintType, Any>().apply {
            put(DecodeHintType.POSSIBLE_FORMATS, listOf(BarcodeFormat.QR_CODE))
            put(DecodeHintType.ALSO_INVERTED, true)
        }
    }

    private val encodeHint by lazy {
        hashMapOf<EncodeHintType, Any>().apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q)
            put(EncodeHintType.MARGIN, 0)
        }
    }

    private fun decode(source: LuminanceSource): String {
        val bitmap = BinaryBitmap(HybridBinarizer(source))
        return QRCodeReader().decode(bitmap, decodeHint).text.trim()
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

    private fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap {
        if (maxHeight <= 0 || maxWidth <= 0) {
            return this
        }

        val maxRatio = maxWidth.toFloat() / maxHeight
        val ratio = width.toFloat() / height

        var width = maxWidth
        var height = maxHeight
        if (maxRatio > 1) {
            width = (maxHeight.toFloat() * ratio).toInt()
        } else {
            height = (maxWidth.toFloat() / ratio).toInt()
        }

        return Bitmap.createScaledBitmap(this, width, height, true)
    }

    fun decodeFromStream(stream: InputStream): String {
        var bitmap = requireNotNull(
            BitmapFactory.decodeStream(
                stream,
                null,
                BitmapFactory.Options()
            )
        ) { "Unable to decode stream to bitmap" }

        for (i in 0..2) {
            if (i != 0) {
                bitmap = bitmap.resize(bitmap.width / (i * 2), bitmap.height / (i * 2))
            }

            try {
                val pixels = IntArray(bitmap.width * bitmap.height)
                bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
                return decode(source = RGBLuminanceSource(bitmap.width, bitmap.height, pixels))
            } catch (_: NotFoundException) {
            }
        }

        throw IllegalArgumentException(stream.toString())
    }

    private fun createBitmap(
        bitMatrix: BitMatrix,
        @ColorInt foregroundColor: Int,
        @ColorInt backgroundColor: Int
    ): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix[x, y]) foregroundColor else backgroundColor
            }
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    fun encodeToBitmap(
        contents: String,
        size: Int,
        @ColorInt foregroundColor: Int = Color.WHITE,
        @ColorInt backgroundColor: Int = Color.BLACK,
    ) = createBitmap(
        bitMatrix = QRCodeWriter().encode(contents, BarcodeFormat.QR_CODE, size, size, encodeHint),
        foregroundColor = foregroundColor,
        backgroundColor = backgroundColor
    )
}