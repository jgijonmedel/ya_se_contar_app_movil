package com.janus.aprendiendonumeros.ui.utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.janus.aprendiendonumeros.R
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.net.URLConnection

class UIDesign {

    companion object {
        fun getSize(context: Context, totalImages: Int): Int {
            val imageViewSize = when {
                (totalImages < 3) -> context.resources.getDimension(R.dimen.img_size_large)
                (totalImages < 5) -> context.resources.getDimension(R.dimen.img_size_medium)
                else -> context.resources.getDimension(R.dimen.img_size_small)
            }
            return imageViewSize.toInt()
        }

        fun getBitmapFromURL(src: String): Bitmap? {
            var bm: Bitmap? = null
            try {
                val url: URL = URL(src)
                val urlConnection: URLConnection = url.openConnection()
                urlConnection.connect()
                val inputStream: InputStream = urlConnection.getInputStream()
                val bufferedInputStream: BufferedInputStream = BufferedInputStream(inputStream)
                bm = BitmapFactory.decodeStream(bufferedInputStream)
                bufferedInputStream.close()
                inputStream.close()
            } catch (e: IOException) {
                // Log exception
                return null
            }
            return bm
        }
    }
}