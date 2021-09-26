package project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import project.projectfive.panoramaviewerproject.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10

class TextureObject(cntx: Context) {
    private val context: Context
    lateinit private var vertexData: FloatBuffer
    private var aPositionLocation = 0
    private var aTextureLocation = 0
    private var uTextureUnitLocation = 0
    private var uMatrixLocation = 0
    private var programId = 0
    private var texture = 0
    init {
        context = cntx
        createProgram()
        locations
        prepareData()
        bindData()
    }



    private fun prepareData() {
        val vertices = floatArrayOf(
            -1f,
            1f,
            1f,
            0f,
            0f,
            -1f,
            -1f,
            1f,
            0f,
            1f,
            1f,
            1f,
            1f,
            1f,
            0f,
            1f,
            -1f,
            1f,
            1f,
            1f
        )
        vertexData = ByteBuffer
            .allocateDirect(vertices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        vertexData.put(vertices)
        texture = TextureUtils.loadTexture(context, R.drawable.box)
    }

    private fun createProgram() {
        val vertexShaderId =
            ShaderUtils.createShader(context, GLES20.GL_VERTEX_SHADER, R.raw.vertex_shader)
        val fragmentShaderId =
            ShaderUtils.createShader(context, GLES20.GL_FRAGMENT_SHADER, R.raw.fragment_shader)
        programId = ShaderUtils.createProgram(vertexShaderId, fragmentShaderId)
        GLES20.glUseProgram(programId)

    }

    private val locations: Unit
        private get() {
            aPositionLocation = GLES20.glGetAttribLocation(programId, "a_Position")
            aTextureLocation = GLES20.glGetAttribLocation(programId, "a_Texture")
            uTextureUnitLocation = GLES20.glGetUniformLocation(programId, "u_TextureUnit")
            uMatrixLocation = GLES20.glGetUniformLocation(programId, "u_Matrix")
        }

    fun draw(mvpMatrix: FloatArray?){
        GLES20.glUseProgram(programId)
        aPositionLocation = GLES20.glGetAttribLocation(programId, "a_Position")
        GLES20.glEnableVertexAttribArray(aPositionLocation)
        GLES20.glVertexAttribPointer(
            aTextureLocation, TEXTURE_COUNT, GLES20.GL_FLOAT,
            false, STRIDE, vertexData
        )
        GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, mvpMatrix, 0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
    }

    private fun bindData() {
        // координаты вершин
        vertexData.position(0)
        GLES20.glVertexAttribPointer(
            aPositionLocation, POSITION_COUNT, GLES20.GL_FLOAT,
            false, STRIDE, vertexData
        )
        GLES20.glEnableVertexAttribArray(aPositionLocation)

        // координаты текстур
        vertexData.position(POSITION_COUNT)
        GLES20.glVertexAttribPointer(
            aTextureLocation, TEXTURE_COUNT, GLES20.GL_FLOAT,
            false, STRIDE, vertexData
        )
        GLES20.glEnableVertexAttribArray(aTextureLocation)

        // помещаем текстуру в target 2D юнита 0
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture)

        // юнит текстуры
        GLES20.glUniform1i(uTextureUnitLocation, 0)
    }




    companion object {
        private const val POSITION_COUNT = 3
        private const val TEXTURE_COUNT = 2
        private const val STRIDE = (POSITION_COUNT
                + TEXTURE_COUNT) * 4
    }

}