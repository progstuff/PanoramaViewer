package project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLES20.*
import android.opengl.GLUtils


object TextureUtils {
    fun loadTexture(context: Context, resourceId: Int): Int {
        // создание объекта текстуры
        val textureIds = IntArray(1)
        glGenTextures(1, textureIds, 0)
        if (textureIds[0] == 0) {
            return 0
        }

        // получение Bitmap
        val options = BitmapFactory.Options()
        options.inScaled = false
        val bitmap = BitmapFactory.decodeResource(
            context.getResources(), resourceId, options
        )
        if (bitmap == null) {
            glDeleteTextures(1, textureIds, 0)
            return 0
        }

        // настройка объекта текстуры
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, textureIds[0])
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        // сброс target
        glBindTexture(GL_TEXTURE_2D, 0)
        return textureIds[0]
    }
}