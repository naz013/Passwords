package com.cray.software.passwords.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


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
        private val HIGH_QUALITY = 100

        @JvmStatic
        fun getCompressed(bitmap: Bitmap?): ByteArray? {
            return if (bitmap != null) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, HIGH_QUALITY, byteArrayOutputStream)
                byteArrayOutputStream.toByteArray()
            } else
                null
        }

        @Throws(FileNotFoundException::class)
        @JvmStatic
        fun decodeUriToBitmap(context: Context, uri: Uri): Bitmap {
            val imageStream = context.contentResolver.openInputStream(uri)
            return BitmapFactory.decodeStream(imageStream)
        }
    }
}