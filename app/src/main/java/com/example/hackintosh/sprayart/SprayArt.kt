package com.example.hackintosh.sprayart

import android.content.Context
import android.content.CursorLoader
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.ViewGroup
import android.view.WindowManager
import cn.easyar.Engine
import kotlinx.android.synthetic.main.activity_spray_art.*
import android.provider.MediaStore
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.FileProvider
import android.view.View
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class SprayArt: AppCompatActivity() {
    companion object {
        private val key = "A0vDMZQiBS0eCmR7aoXSL9i9HdhFYZHPoEMvo3d1Hlvlh31QZWWuUD2ZuCsJ6onM6ifuEr8JoU2VEn0gFPEyshbNoQ482Aob1Qtxa3rbaN2JNw700DsJBMQbQJb3M6wybdWFvkIkKZSLF2pxYVvpUEK22zW1NuBuYzz6PTYasrEXZvjDLhQMuI4a006pKUdGrgppOCBB\n\n"
    }

    private var glView: GLView? = null
    private val REQUEST_IMAGE_CAPTURE = 100
    private val REQUEST_SELECT_PICTURE = 101
    private var mCurrentPhotoPath = ""
    private val helper by lazy { TextureHelper(BitmapFactory.decodeResource(resources, R.drawable.sausages))}
    private val photosMap = mutableMapOf<String, String>()
    private var selectedGrafitti : Uri = Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spray_art)
        window.setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.")
        }

        glView = GLView(this, photosMap)
        preview.addView(glView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        addWallBtn.setOnClickListener {
            dispatchTakePictureIntent()
        }
        addGrafityBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_SELECT_PICTURE)
        }

        doneBtn.setOnClickListener {
            plusBtn.visibility = View.GONE
            minusBtn.visibility = View.GONE
            doneBtn.visibility = View.GONE
            imageView.visibility = View.GONE
            photosMap[selectedGrafitti.toString()] = mCurrentPhotoPath
            reloadGlView()
            //save data to server here
        }
    }

    override fun onPause() {
        super.onPause()
        preview.removeView(glView)
    }

    override fun onResume() {
        super.onResume()
        if (glView!!.parent == null)
            preview.addView(glView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == FragmentActivity.RESULT_OK)
        {
            imageView.visibility = View.VISIBLE
            imageView.setImageBitmap(BitmapFactory.decodeFile(mCurrentPhotoPath))

        } else if(requestCode == REQUEST_SELECT_PICTURE && resultCode == RESULT_OK) {
            plusBtn.visibility = View.VISIBLE
            minusBtn.visibility = View.VISIBLE
            doneBtn.visibility = View.VISIBLE
            selectedGrafitti = data!!.data


        }
    }

    private fun reloadGlView()
    {
        preview.removeView(glView)
        glView = GLView(this, photosMap)
        preview.addView(glView)
    }

    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile : File = createImageFile()
            val photoURI = FileProvider.getUriForFile(this@SprayArt, "com.example.hackintosh.sprayart.fileprovider", photoFile)
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }
}