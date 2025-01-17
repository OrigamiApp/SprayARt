package com.example.hackintosh.sprayart

import android.content.Context
import android.graphics.Bitmap
import android.opengl.GLSurfaceView
import cn.easyar.Engine
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import javax.microedition.khronos.opengles.GL10

class GLView(context: Context, photosMap : MutableMap<String, String>) : GLSurfaceView(context) {
    private val easyAR: EasyAR

    init {
        setEGLContextFactory(ContextFactory())
        setEGLConfigChooser(ConfigChooser())

        easyAR = EasyAR(context, photosMap)

        this.setRenderer(object : GLSurfaceView.Renderer {
            override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
                synchronized(easyAR) {
                    easyAR.initGL()
                }
            }

            override fun onSurfaceChanged(gl: GL10, w: Int, h: Int) {
                synchronized(easyAR) {
                    easyAR.resizeGL(w, h)
                }
            }

            override fun onDrawFrame(gl: GL10) {
                synchronized(easyAR) {
                    easyAR.render()
                }
            }
        })
        this.setZOrderMediaOverlay(true)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        synchronized(easyAR) {
            if (easyAR.initialize()) {
                easyAR.start()
            }
        }
    }

    override fun onDetachedFromWindow() {
        synchronized(easyAR) {
            easyAR.stop()
            easyAR.dispose()
        }
        super.onDetachedFromWindow()
    }

    override fun onResume() {
        super.onResume()
        Engine.onResume()
    }

    override fun onPause() {
        Engine.onPause()
        super.onPause()
    }

    private class ContextFactory : GLSurfaceView.EGLContextFactory {

        override fun createContext(egl: EGL10, display: EGLDisplay, eglConfig: EGLConfig): EGLContext {
            val context: EGLContext
            val attrib = intArrayOf(EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE)
            context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib)
            return context
        }

        override fun destroyContext(egl: EGL10, display: EGLDisplay, context: EGLContext) {
            egl.eglDestroyContext(display, context)
        }

        companion object {
            private val EGL_CONTEXT_CLIENT_VERSION = 0x3098
        }
    }

    private class ConfigChooser : GLSurfaceView.EGLConfigChooser {
        override fun chooseConfig(egl: EGL10, display: EGLDisplay): EGLConfig {
            val EGL_OPENGL_ES2_BIT = 0x0004
            val attrib = intArrayOf(EGL10.EGL_RED_SIZE, 4, EGL10.EGL_GREEN_SIZE, 4, EGL10.EGL_BLUE_SIZE, 4, EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT, EGL10.EGL_NONE)

            val num_config = IntArray(1)
            egl.eglChooseConfig(display, attrib, null, 0, num_config)

            val numConfigs = num_config[0]
            if (numConfigs <= 0)
                throw IllegalArgumentException("fail to choose EGL configs")

            val configs = arrayOfNulls<EGLConfig>(numConfigs)
            egl.eglChooseConfig(display, attrib, configs, numConfigs,
                    num_config)

            for (config in configs) {
                val `val` = IntArray(1)
                var r = 0
                var g = 0
                var b = 0
                var a = 0
                var d = 0
                if (egl.eglGetConfigAttrib(display, config, EGL10.EGL_DEPTH_SIZE, `val`))
                    d = `val`[0]
                if (d < 16)
                    continue

                if (egl.eglGetConfigAttrib(display, config, EGL10.EGL_RED_SIZE, `val`))
                    r = `val`[0]
                if (egl.eglGetConfigAttrib(display, config, EGL10.EGL_GREEN_SIZE, `val`))
                    g = `val`[0]
                if (egl.eglGetConfigAttrib(display, config, EGL10.EGL_BLUE_SIZE, `val`))
                    b = `val`[0]
                if (egl.eglGetConfigAttrib(display, config, EGL10.EGL_ALPHA_SIZE, `val`))
                    a = `val`[0]
                if (r == 8 && g == 8 && b == 8 && a == 0)
                    return config!!
            }

            return configs[0]!!
        }
    }
}