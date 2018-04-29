package com.example.hackintosh.sprayart

import android.opengl.GLES20
import android.R.attr.data
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import cn.easyar.Vec2F
import cn.easyar.Matrix44F
import cn.easyar.TargetInstance
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer


class ImageRenderer(val context : Context) {
    private var shaderProgram: Int = 0
    private var posCoord: Int = 0
    private var posTex: Int = 0
    private var posTrans: Int = 0
    private var posProj: Int = 0

    private var vboCoord: Int = 0
    private var vboTex: Int = 0
    private var vboFaces: Int = 0

    private val TAG = "ImageRenderer"

    private val vertexShaderProgram = ("uniform mat4 trans;\n"
            + "uniform mat4 proj;\n"
            + "attribute vec4 coord;\n"
            + "attribute vec2 texcoord;\n"
            + "varying vec2 vtexcoord;\n"
            + "\n"
            + "void main(void)\n"
            + "{\n"
            + "    vtexcoord = texcoord;\n"
            + "    gl_Position = proj*trans*coord;\n"
            + "}\n"
            + "\n")

    private val fragmentShaderProgram = ("#ifdef GL_ES\n"
            + "precision highp float;\n"
            + "#endif\n"
            + "varying vec2 vtexcoord;\n"
            + "uniform sampler2D texture;\n"
            + "\n"
            + "void main(void)\n"
            + "{\n"
            + "    gl_FragColor = texture2D(texture, vtexcoord);\n"
            + "}\n"
            + "\n")

    private fun flatten(a: Array<FloatArray>): FloatArray {
        var size = 0
        run {
            var k = 0
            while (k < a.size) {
                size += a[k].size
                k += 1
            }
        }
        val l = FloatArray(size)
        var offset = 0
        var k = 0
        while (k < a.size) {
            System.arraycopy(a[k], 0, l, offset, a[k].size)
            offset += a[k].size
            k += 1
        }
        return l
    }

    private fun flatten(a: Array<IntArray>): IntArray {
        var size = 0
        run {
            var k = 0
            while (k < a.size) {
                size += a[k].size
                k += 1
            }
        }
        val l = IntArray(size)
        var offset = 0
        var k = 0
        while (k < a.size) {
            System.arraycopy(a[k], 0, l, offset, a[k].size)
            offset += a[k].size
            k += 1
        }
        return l
    }

    private fun flatten(a: Array<ShortArray>): ShortArray {
        var size = 0
        run {
            var k = 0
            while (k < a.size) {
                size += a[k].size
                k += 1
            }
        }
        val l = ShortArray(size)
        var offset = 0
        var k = 0
        while (k < a.size) {
            System.arraycopy(a[k], 0, l, offset, a[k].size)
            offset += a[k].size
            k += 1
        }
        return l
    }

    private fun flatten(a: Array<ByteArray>): ByteArray {
        var size = 0
        run {
            var k = 0
            while (k < a.size) {
                size += a[k].size
                k += 1
            }
        }
        val l = ByteArray(size)
        var offset = 0
        var k = 0
        while (k < a.size) {
            System.arraycopy(a[k], 0, l, offset, a[k].size)
            offset += a[k].size
            k += 1
        }
        return l
    }

    private fun byteArrayFromIntArray(a: IntArray): ByteArray {
        val l = ByteArray(a.size)
        var k = 0
        while (k < a.size) {
            l[k] = (a[k] and 0xFF).toByte()
            k += 1
        }
        return l
    }

    private fun generateOneBuffer(): Int {
        val buffer = intArrayOf(0)
        GLES20.glGenBuffers(1, buffer, 0)
        return buffer[0]
    }

    private fun generateOneTexture(): Int {
        val buffer = intArrayOf(0)
        GLES20.glGenTextures(1, buffer, 0)
        return buffer[0]
    }

    fun init() {
        shaderProgram = GLES20.glCreateProgram()
        val vertShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
        GLES20.glShaderSource(vertShader, vertexShaderProgram)
        GLES20.glCompileShader(vertShader)
        val fragShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
        GLES20.glShaderSource(fragShader, fragmentShaderProgram)
        GLES20.glCompileShader(fragShader)
        GLES20.glAttachShader(shaderProgram, vertShader)
        GLES20.glAttachShader(shaderProgram, fragShader)
        GLES20.glLinkProgram(shaderProgram)
        GLES20.glUseProgram(shaderProgram)
        posCoord = GLES20.glGetAttribLocation(shaderProgram, "coord")
        posTex = GLES20.glGetAttribLocation(shaderProgram, "texcoord")
        posTrans = GLES20.glGetUniformLocation(shaderProgram, "trans")
        posProj = GLES20.glGetUniformLocation(shaderProgram, "proj")

        vboCoord = generateOneBuffer()
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboCoord)
        val cube_vertices = arrayOf(floatArrayOf(1.0f / 2, 1.0f / 2, 0f), floatArrayOf(1.0f / 2, -1.0f / 2, 0f), floatArrayOf(-1.0f / 2, -1.0f / 2, 0f), floatArrayOf(-1.0f / 2, 1.0f / 2, 0f))
        val cube_vertices_buffer = FloatBuffer.wrap(flatten(cube_vertices))
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertices_buffer.limit() * 4, cube_vertices_buffer, GLES20.GL_DYNAMIC_DRAW)

        vboTex = generateOneBuffer()
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboTex)
        val cube_vertex_colors = arrayOf(intArrayOf(0, 0), intArrayOf(0, 1), intArrayOf(1, 1), intArrayOf(1, 0))
        val cube_vertex_colors_buffer = ByteBuffer.wrap(byteArrayFromIntArray(flatten(cube_vertex_colors)))
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertex_colors_buffer.limit(), cube_vertex_colors_buffer, GLES20.GL_STATIC_DRAW)

        vboFaces = generateOneBuffer()
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboFaces)
        val cube_faces = shortArrayOf(3, 2, 1, 0)
        val cube_faces_buffer = ShortBuffer.wrap(cube_faces)
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, cube_faces_buffer.limit() * 2, cube_faces_buffer, GLES20.GL_STATIC_DRAW)


        GLES20.glUniform1i(GLES20.glGetUniformLocation(shaderProgram, "texture"), 0)

        //textureHelper.texture = generateOneTexture()
        //GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHelper.texture)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
    }

    fun render(projectionMatrix: Matrix44F, targetInstance: TargetInstance, size: Vec2F) {
        Log.e("ImageRenderer", "render")

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboCoord)
        //val image = BitmapFactory.decodeResource(context.resources, targetInstance.target().name().toInt())
        val image = MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.parse(targetInstance.target().name()))
        val helper = TextureHelper(image)

        val cube_vertices = arrayOf(floatArrayOf(helper.bmWidth, helper.bmHeight, 0f), floatArrayOf(helper.bmWidth, -helper.bmHeight, 0f), floatArrayOf(-helper.bmWidth, -helper.bmHeight, 0f), floatArrayOf(-helper.bmWidth, helper.bmHeight, 0f))
        val cube_vertices_buffer = FloatBuffer.wrap(flatten(cube_vertices))
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, cube_vertices_buffer.limit() * 4, cube_vertices_buffer, GLES20.GL_DYNAMIC_DRAW)

        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glUseProgram(shaderProgram)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboCoord)
        GLES20.glEnableVertexAttribArray(posCoord)
        GLES20.glVertexAttribPointer(posCoord, 3, GLES20.GL_FLOAT, false, 0, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboTex)
        GLES20.glEnableVertexAttribArray(posTex)
        GLES20.glVertexAttribPointer(posTex, 2, GLES20.GL_UNSIGNED_BYTE, false, 0, 0)
        GLES20.glUniformMatrix4fv(posTrans, 1, false, targetInstance.poseGL().data, 0)
        GLES20.glUniformMatrix4fv(posProj, 1, false, projectionMatrix.data, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboFaces)

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, vboFaces)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)

        helper.updateTexture()

        GLES20.glDrawElements(GLES20.GL_TRIANGLE_FAN, 4, GLES20.GL_UNSIGNED_SHORT, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }

}