package com.example.hackintosh.sprayart

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import android.util.Log


object TextureHelper {
    private val TAG = "TextureHelper"

    var texture: Int = 0
    private var curBitmap: Bitmap? = null

    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureHandle = IntArray(1)

        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error generating texture name.")
        }

        val options = BitmapFactory.Options()
        options.inScaled = false    // No pre-scaling

        // Read in the resource
        val bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options)

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle()

        return textureHandle[0]
    }

    fun loadTexture(context: Context, bitmap: Bitmap): Int {
        val textureHandle = IntArray(1)

        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error generating texture name.")
        }

        val options = BitmapFactory.Options()
        options.inScaled = false    // No pre-scaling

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle()

        return textureHandle[0]
    }

    fun loadTexture(context: Context, path: String): Int {
        val textureHandle = IntArray(1)

        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] == 0) {
            throw RuntimeException("Error generating texture name.")
        }

        val options = BitmapFactory.Options()
        options.inScaled = false    // No pre-scaling

        // Read in the resource
        val bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.peace_graffiti)

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

        // Set filtering
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST)

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)

        // Recycle the bitmap, since its data has been loaded into OpenGL.
        bitmap.recycle()

        return textureHandle[0]
    }

    fun updateTexture() {
        Log.e("Update", "texture")
        val bitmap1: Bitmap
        if (curBitmap == null) {
            bitmap1 = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
        } else
            bitmap1 = Bitmap.createBitmap(curBitmap!!)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap1, 0)
        //bitmap1.recycle()
    }

    fun updateBitmap(bitmap: Bitmap) {
        curBitmap = bitmap
    }
}