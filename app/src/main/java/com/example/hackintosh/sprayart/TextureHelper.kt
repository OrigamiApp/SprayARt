package com.example.hackintosh.sprayart

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLES20
import android.opengl.GLUtils
import android.graphics.BitmapFactory
import android.util.Log

class TextureHelper (private var bitmap: Bitmap){

    var ratio : Float = 0.2f / bitmap.height
    set(value) {
        field = value
        bmHeight = bitmap.height.toFloat() * ratio
        bmWidth = bitmap.width.toFloat() * ratio
    }
    var texture: Int = 0
    var bmHeight : Float = bitmap.height.toFloat() * ratio
    var bmWidth : Float = bitmap.width.toFloat() * ratio

    fun updateTexture() {
        Log.e("Update", "texture")
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
    }
}