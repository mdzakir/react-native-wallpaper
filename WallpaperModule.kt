package com.mdzakir.reactnative.wallpaper

import android.app.WallpaperManager
import android.graphics.Rect
import android.os.Build
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.util.DisplayMetrics
import android.view.WindowManager
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL

class WallpaperModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    override fun getName(): String {
        return "RNWallpaper"
    }

    @ReactMethod
    fun setWallpaper(uri: String, options: ReadableMap, promise: Promise) {
        try {
            val wallpaperManager = WallpaperManager.getInstance(reactContext)

            // Load bitmap
            val bitmap = getBitmapFromUri(uri) ?: run {
                promise.reject("ERROR", "Failed to load image")
                return
            }

            // Get the actual screen dimensions instead of wallpaper desired minimum
            val displayMetrics = DisplayMetrics()
            val windowManager = reactContext.getSystemService(ReactApplicationContext.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            
            val screenWidth = displayMetrics.widthPixels
            val screenHeight = displayMetrics.heightPixels
            
            Log.d(TAG, "Screen dimensions: $screenWidth x $screenHeight")
            Log.d(TAG, "Original bitmap dimensions: ${bitmap.width} x ${bitmap.height}")

            // Scale bitmap to fit the screen without distortion
            val bitmapRatio = bitmap.width.toFloat() / bitmap.height
            val screenRatio = screenWidth.toFloat() / screenHeight
            
            // Determine scaling to completely fill the screen
            val scaledBitmap: Bitmap
            val matrix = Matrix()
            
            if (bitmapRatio > screenRatio) {
                // Image is wider than screen ratio - scale to match height
                val scaleFactor = screenHeight.toFloat() / bitmap.height
                matrix.postScale(scaleFactor, scaleFactor)
            } else {
                // Image is taller than screen ratio - scale to match width
                val scaleFactor = screenWidth.toFloat() / bitmap.width
                matrix.postScale(scaleFactor, scaleFactor)
            }
            
            scaledBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
            
            Log.d(TAG, "Scaled bitmap dimensions: ${scaledBitmap.width} x ${scaledBitmap.height}")

            // Calculate the crop to center the image
            val xOffset = Math.max(0, (scaledBitmap.width - screenWidth) / 2)
            val yOffset = Math.max(0, (scaledBitmap.height - screenHeight) / 2)
            
            Log.d(TAG, "Crop offsets: x=$xOffset, y=$yOffset")

            // Create a perfectly centered crop rectangle
            val visibleCropHint = Rect(
                xOffset, 
                yOffset,
                xOffset + screenWidth,
                yOffset + screenHeight
            )
            
            // Apply wallpaper with proper flags
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                var flags = 0
                val isSystem = if (options.hasKey("isSystem")) options.getBoolean("isSystem") else true
                val isLock = if (options.hasKey("isLock")) options.getBoolean("isLock") else false
                
                if (isSystem) flags = flags or WallpaperManager.FLAG_SYSTEM
                if (isLock) flags = flags or WallpaperManager.FLAG_LOCK
                if (flags == 0) flags = WallpaperManager.FLAG_SYSTEM  // Default to system if none specified
                
                Log.d(TAG, "Setting wallpaper with flags: $flags (isSystem: $isSystem, isLock: $isLock)")
                wallpaperManager.setBitmap(scaledBitmap, visibleCropHint, true, flags)
            } else {
                wallpaperManager.setBitmap(scaledBitmap)
            }

            promise.resolve("Wallpaper set successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error setting wallpaper: ${e.message}")
            e.printStackTrace()
            promise.reject("ERROR", "Failed to set wallpaper: ${e.message}")
        }
    }

    // Helper method to load bitmap from URI (could be a file or remote URL)
    private fun getBitmapFromUri(uri: String): Bitmap? {
        return try {
            if (uri.startsWith("http")) {
                val url = URL(uri)
                val inputStream = url.openStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream.close()
                bitmap
            } else {
                BitmapFactory.decodeFile(uri)
            }
        } catch (e: IOException) {
            Log.e(TAG, "Error loading bitmap: ${e.message}")
            null
        }
    }

    companion object {
        private const val TAG = "WallpaperModule"
    }
}