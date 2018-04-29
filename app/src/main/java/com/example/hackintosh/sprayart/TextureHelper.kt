package com.example.hackintosh.sprayart

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import android.util.Log

class TextureHelper (private var bitmap: Bitmap){

    var texture: Int = 0
    var bmHeight : Float = 0.5f
    var bmWidth : Float =  (bitmap.height.toFloat() * bmHeight) / bitmap.width.toFloat()

    fun updateTexture() {
        Log.e("Update", "texture")
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
    }

    fun updateBitmap(newBitmap: Bitmap) {
        bitmap = newBitmap
        bmWidth = (bitmap.height.toFloat() * bmHeight) / bitmap.width.toFloat()
    }

    fun updateWidth() {
        bmWidth = (bitmap.height.toFloat() * bmHeight) / bitmap.width.toFloat()
    }
}