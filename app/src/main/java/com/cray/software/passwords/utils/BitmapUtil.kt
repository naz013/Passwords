package com.cray.software.passwords.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Base64
import android.util.Base64OutputStream
import java.io.*


/**
 * Copyright 2017 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class BitmapUtil {

    companion object {
        private val MAX_ZISE: Long = 768500
        private val REQUIRED_SIZE = 350
        private val HIGH_QUALITY = 100

        @Throws(FileNotFoundException::class)
        fun decodeUri(context: Context, selectedImage: Uri): Bitmap {
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(selectedImage), null, o)
            val REQUIRED_SIZE = 350
            var width_tmp = o.outWidth
            var height_tmp = o.outHeight
            var scale = 1
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                    break
                }
                width_tmp /= 2
                height_tmp /= 2
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            return BitmapFactory.decodeStream(context.contentResolver.openInputStream(selectedImage), null, o2)
        }

        @JvmStatic
        fun getCompressed(bitmap: Bitmap?): ByteArray? {
            if (bitmap != null) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, HIGH_QUALITY, byteArrayOutputStream)
                val byteArrayImage = byteArrayOutputStream.toByteArray()
//                val length = byteArrayImage.size.toLong()
//                if (length > MAX_ZISE) {
//                    val scalar = length.toDouble() / MAX_ZISE.toDouble()
//                    val coef = (HIGH_QUALITY.toDouble() / scalar).toInt()
//                    val byteStream = ByteArrayOutputStream()
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, coef, byteStream)
//                    return byteStream.toByteArray()
//                } else {
                    return byteArrayImage
//                }
            } else
                return null
        }

        fun getCompressedBitmap(bitmap: Bitmap?): Bitmap? {
            if (bitmap != null) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, HIGH_QUALITY, byteArrayOutputStream)
                val byteArrayImage = byteArrayOutputStream.toByteArray()
                val length = byteArrayImage.size.toLong()
                if (length > MAX_ZISE) {
                    val scalar = length.toDouble() / MAX_ZISE.toDouble()
                    val coef = (HIGH_QUALITY.toDouble() / scalar).toInt()
                    val byteStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, coef, byteStream)
                    return BitmapFactory.decodeStream(ByteArrayInputStream(byteStream.toByteArray()))
                } else {
                    return bitmap
                }
            }
            return null
        }

        fun imageOrientationValidator(bitmap: Bitmap, path: String): Bitmap {
            var bmp = bitmap
            val ei: ExifInterface
            try {
                ei = ExifInterface(path)
                val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL)
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> bmp = rotateImage(bmp, 90f)
                    ExifInterface.ORIENTATION_ROTATE_180 -> bmp = rotateImage(bmp, 180f)
                    ExifInterface.ORIENTATION_ROTATE_270 -> bmp = rotateImage(bmp, 270f)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }

            return bmp
        }

        private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
            var bitmap: Bitmap? = null
            val matrix = Matrix()
            matrix.postRotate(angle)
            try {
                bitmap = Bitmap.createBitmap(source, 0, 0, source.width, source.height,
                        matrix, true)
            } catch (err: OutOfMemoryError) {
                err.printStackTrace()
            }
            return bitmap!!
        }

        fun getEncodedBitmap(bitmap: Bitmap): String {
            val byteImage = getCompressed(bitmap)
            val inputStream = ByteArrayInputStream(byteImage)
            val buffer = ByteArray(8192)
            var bytesRead: Int
            val output = ByteArrayOutputStream()
            val output64 = Base64OutputStream(output, Base64.DEFAULT)
            try {
                do {
                    bytesRead = inputStream.read(buffer)
                    output64.write(buffer, 0, bytesRead)
                } while (bytesRead != -1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            output64.close()
            return output.toString()
        }

        fun getDecodedBitmap(bitmap: String): ByteArray {
            return Base64.decode(bitmap, Base64.DEFAULT)
        }

        @Throws(IOException::class) fun getBytes(inputStream: InputStream): ByteArray {
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var len: Int = 0
            while (len != -1) {
                len = inputStream.read(buffer)
                byteBuffer.write(buffer, 0, len)
            }
            return byteBuffer.toByteArray()
        }

        fun getEncodedFile(context: Context, uri: Uri): String? {
            try {
                val iStream = context.contentResolver.openInputStream(uri)
                val inputData = getBytes(iStream)
                return Base64.encodeToString(inputData, Base64.DEFAULT)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        fun getFileContent(context: Context, uri: Uri): ByteArray {
            try {
                val iStream = context.contentResolver.openInputStream(uri)
                val inputData = getBytes(iStream)
                return inputData
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return ByteArray(0)
        }

        @Throws(FileNotFoundException::class)
        @JvmStatic fun decodeUriToBitmap(context: Context, uri: Uri): Bitmap {
            val imageStream = context.contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(imageStream)
        }
    }
}