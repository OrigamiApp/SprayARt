package com.example.hackintosh.sprayart

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import cn.easyar.Engine

class SprayArt: AppCompatActivity() {
    companion object {
        private val key = "A0vDMZQiBS0eCmR7aoXSL9i9HdhFYZHPoEMvo3d1Hlvlh31QZWWuUD2ZuCsJ6onM6ifuEr8JoU2VEn0gFPEyshbNoQ482Aob1Qtxa3rbaN2JNw700DsJBMQbQJb3M6wybdWFvkIkKZSLF2pxYVvpUEK22zW1NuBuYzz6PTYasrEXZvjDLhQMuI4a006pKUdGrgppOCBB\n\n"
    }

    private var glView: GLView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spray_art)
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.")
        }
        TextureHelper.updateBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.peace_graffiti))
        glView = GLView(this)

        ((findViewById(R.id.preview) as ViewGroup).addView(glView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)))
    }
}