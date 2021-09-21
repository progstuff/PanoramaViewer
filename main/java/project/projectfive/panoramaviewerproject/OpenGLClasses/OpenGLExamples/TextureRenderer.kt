package project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples


import android.content.Context
import android.opengl.EGLConfig
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples.ShaderUtils.createProgram
import project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples.ShaderUtils.createShader
import project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples.TextureUtils.loadTexture
import project.projectfive.panoramaviewerproject.R
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10


class TextureRenderer(context: Context) : GLSurfaceView.Renderer {
    private val context: Context
    lateinit private var vertexData: FloatBuffer
    private var aPositionLocation = 0
    private var aTextureLocation = 0
    private var uTextureUnitLocation = 0
    private var uMatrixLocation = 0
    private var programId = 0
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mMatrix = FloatArray(16)
    private var texture = 0
    override fun onSurfaceCreated(p0: GL10?, p1: javax.microedition.khronos.egl.EGLConfig?) {
        glClearColor(0f, 0f, 0f, 1f)
        glEnable(GL_DEPTH_TEST)
        createAndUseProgram()
        locations
        prepareData()
        bindData()
        createViewMatrix()
    }

    override fun onSurfaceChanged(arg0: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        createProjectionMatrix(width, height)
        bindMatrix()
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
        texture = loadTexture(context, R.drawable.box)
    }

    private fun createAndUseProgram() {
        val vertexShaderId =
            createShader(context, GL_VERTEX_SHADER, R.raw.vertex_shader)
        val fragmentShaderId =
            createShader(context, GL_FRAGMENT_SHADER, R.raw.fragment_shader)
        programId = createProgram(vertexShaderId, fragmentShaderId)
        glUseProgram(programId)
    }

    private val locations: Unit
        private get() {
            aPositionLocation = glGetAttribLocation(programId, "a_Position")
            aTextureLocation = glGetAttribLocation(programId, "a_Texture")
            uTextureUnitLocation = glGetUniformLocation(programId, "u_TextureUnit")
            uMatrixLocation = glGetUniformLocation(programId, "u_Matrix")
        }

    private fun bindData() {
        // координаты вершин
        vertexData.position(0)
        glVertexAttribPointer(
            aPositionLocation, POSITION_COUNT, GL_FLOAT,
            false, STRIDE, vertexData
        )
        glEnableVertexAttribArray(aPositionLocation)

        // координаты текстур
        vertexData.position(POSITION_COUNT)
        glVertexAttribPointer(
            aTextureLocation, TEXTURE_COUNT, GL_FLOAT,
            false, STRIDE, vertexData
        )
        glEnableVertexAttribArray(aTextureLocation)

        // помещаем текстуру в target 2D юнита 0
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture)

        // юнит текстуры
        glUniform1i(uTextureUnitLocation, 0)
    }

    private fun createProjectionMatrix(width: Int, height: Int) {
        var ratio = 1f
        var left = -1f
        var right = 1f
        var bottom = -1f
        var top = 1f
        val near = 2f
        val far = 12f
        if (width > height) {
            ratio = width.toFloat() / height
            left *= ratio
            right *= ratio
        } else {
            ratio = height.toFloat() / width
            bottom *= ratio
            top *= ratio
        }
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far)
    }

    private fun createViewMatrix() {
        // точка положения камеры
        val eyeX = 0f
        val eyeY = 0f
        val eyeZ = 7f

        // точка направления камеры
        val centerX = 0f
        val centerY = 0f
        val centerZ = 0f

        // up-вектор
        val upX = 0f
        val upY = 1f
        val upZ = 0f
        Matrix.setLookAtM(
            mViewMatrix,
            0,
            eyeX,
            eyeY,
            eyeZ,
            centerX,
            centerY,
            centerZ,
            upX,
            upY,
            upZ
        )
    }

    private fun bindMatrix() {
        Matrix.multiplyMM(mMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)
        glUniformMatrix4fv(uMatrixLocation, 1, false, mMatrix, 0)
    }

    override fun onDrawFrame(arg0: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4)
    }

    companion object {
        private const val POSITION_COUNT = 3
        private const val TEXTURE_COUNT = 2
        private const val STRIDE = (POSITION_COUNT
                + TEXTURE_COUNT) * 4
    }

    init {
        this.context = context
    }
}