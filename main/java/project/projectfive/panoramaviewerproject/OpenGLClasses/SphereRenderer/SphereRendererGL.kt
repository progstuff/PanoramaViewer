package project.projectfive.panoramaviewerproject.OpenGLClasses.SphereRenderer

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class SphereRendererGL (private var zoom: Int) : GLSurfaceView.Renderer {
    private var programHandle = 0
    private val mModelMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    var xAngle = 0.0f
    var yAngle = 0.0f
    var zAngle = 0.0f

    // floats per trapezoid = 7 floats per vertex * 3 vertices per triangle * 2 triangles per trapezoid
    private val floatsPerTrap = 42

    /** How many bytes per float.  */
    private val mBytesPerFloat = 4

    /** How many elements per vertex.  */
    private val mStrideBytes = 7 * mBytesPerFloat

    /** Offset of the position data.  */
    private val mPositionOffset = 0

    /** Size of the position data in elements.  */
    private val mPositionDataSize = 3

    /** Offset of the color data.  */
    private val mColorOffset = 3

    /** Size of the color data in elements.  */
    private val mColorDataSize = 4

    /** This will be used to pass in the transformation matrix.  */
    private var mMVPMatrixHandle = 0

    /** This will be used to pass in model position information.  */
    private var mPositionHandle = 0

    /** This will be used to pass in model color information.  */
    private var mColorHandle = 0
    fun zoom(z: Int) {
        zoom = z
        setUp()
    }


    override fun onSurfaceCreated(g1: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.5f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glDepthMask(true)
        //Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 2f, 0f, 0f, -5f, 0f, 1f, 0f)
        //Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, 4f, 1f, 0f, 0f, 0f, 1f, 0f)
        programHandle = program
        mMVPMatrixHandle = GLES20.glGetUniformLocation(programHandle, "u_MVPMatrix")
        mPositionHandle = GLES20.glGetAttribLocation(programHandle, "a_Position")
        mColorHandle = GLES20.glGetAttribLocation(programHandle, "a_Color")
        GLES20.glUseProgram(programHandle)
        setUp()
    }

    private fun setUp() {
        //Random r = new Random();

        //Matrix.setLookAtM(mViewMatrix, 0, 0, 0, 2f, 0, 0, -5, 0, 1, 0);
        val verticesData =
            FloatArray(floatsPerTrap * Math.pow(4.0, zoom.toDouble()).toInt())
        var i = 0
        while (i < Math.pow(2.0, zoom.toDouble())) {
            var j = 0
            while (j < Math.pow(2.0, zoom.toDouble())) {
                System.arraycopy(
                    getVertexData(i, j),
                    0,
                    verticesData,
                    (i * Math.pow(2.0, zoom.toDouble()) + j).toInt() * floatsPerTrap,
                    floatsPerTrap
                )
                j++
            }
            i++
        }
        val vertexBuffer: FloatBuffer =
            ByteBuffer.allocateDirect(verticesData.size * mBytesPerFloat)
                .order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertexBuffer.put(verticesData).position(0)
        val vertexBufferIdx: Int
        val buffers = IntArray(1)
        GLES20.glGenBuffers(1, buffers, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers[0])
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            vertexBuffer.capacity() * mBytesPerFloat,
            vertexBuffer,
            GLES20.GL_STATIC_DRAW
        )
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        vertexBufferIdx = buffers[0]
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferIdx)
        GLES20.glEnableVertexAttribArray(mPositionHandle)
        GLES20.glVertexAttribPointer(
            mPositionHandle,
            mPositionDataSize,
            GLES20.GL_FLOAT,
            false,
            mStrideBytes,
            0
        )
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexBufferIdx)
        GLES20.glEnableVertexAttribArray(mColorHandle)
        GLES20.glVertexAttribPointer(
            mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
            mStrideBytes, mPositionDataSize * mBytesPerFloat
        )
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)



//        vertexBuffer.position(mPositionOffset);
//
//
//        GLES20.glVertexAttribPointer(mPositionHandle, mPositionDataSize, GLES20.GL_FLOAT, false,
//                mStrideBytes, vertexBuffer);
//
//
//        GLES20.glEnableVertexAttribArray(mPositionHandle);
//
//        vertexBuffer.position(mColorOffset);
//
//        GLES20.glVertexAttribPointer(mColorHandle, mColorDataSize, GLES20.GL_FLOAT, false,
//                mStrideBytes, vertexBuffer);
//
//        GLES20.glEnableVertexAttribArray(mColorHandle);
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        val ratio = width.toFloat() / height
        val left = -ratio
        val bottom = -1.0f
        val top = 1.0f
        val near = 1.0f
        val far = 10.0f
        Matrix.frustumM(mProjectionMatrix, 0, left, ratio, bottom, top, near, far)
    }



    override fun onDrawFrame(gl: GL10) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        Matrix.setIdentityM(mModelMatrix, 0)
        val time = SystemClock.uptimeMillis() % 10000L
        val angleInDegrees = 360.0f / 5000.0f * time.toInt()

        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0)

        //Matrix.rotateM(mModelMatrix, 0, xAngle, 1.0f, 0.0f, 0.0f)
        //Matrix.rotateM(mModelMatrix, 0, yAngle, 0.0f, 1.0f, 0.0f)
        //Matrix.rotateM(mModelMatrix, 0, zAngle, 0.0f, 0.0f, 1.0f)

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0)
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0)
        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLES,
            0,
            6 * Math.pow(4.0, zoom.toDouble()).toInt()
        )
        val ang = calculateEyeVector(xAngle, yAngle, zAngle)
        Matrix.setLookAtM(mViewMatrix, 0, ang[0]*5f, ang[1]*5f, ang[2]*5f, 0f, 0f, 0f, 0f, 1f, 0f)

        //        for (int i = 0; i < traps.size(); i++) {
//            drawTriangle(traps.get(i).getTri1());
//            drawTriangle(traps.get(i).getTri2());
//        }
    }

    fun rotateByX(ang_rad:Float, point:FloatArray):FloatArray{
        var x = point[0];
        var y = cos(ang_rad)*point[1] - sin(ang_rad)*point[2]
        var z = sin(ang_rad)*point[1] + cos(ang_rad)*point[2]

        return floatArrayOf(x,y,z)
    }

    fun rotateByY(ang_rad:Float, point:FloatArray):FloatArray{
        var x = cos(ang_rad)*point[0] + sin(ang_rad)*point[2];
        var y = point[1]
        var z = -sin(ang_rad)*point[0] + cos(ang_rad)*point[2]

        return floatArrayOf(x,y,z)
    }

    fun rotateByZ(ang_rad:Float, point:FloatArray):FloatArray{
        var x = cos(ang_rad)*point[0] - sin(ang_rad)*point[1]
        var y = sin(ang_rad)*point[0] + cos(ang_rad)*point[1]
        var z = point[2]

        return floatArrayOf(x,y,z)
    }

    fun rotateByVector(ang_rad:Float, point:FloatArray, rotateVector:FloatArray):FloatArray{
        var rx = rotateVector[0]
        var ry = rotateVector[1]
        var rz = rotateVector[2]
        var a11 = cos(ang_rad) + (1-cos(ang_rad))*rx*rx
        var a12 = (1-cos(ang_rad))*rx*ry - sin(ang_rad)*rz
        var a13 = (1-cos(ang_rad))*rx*rz + sin(ang_rad)*ry

        var a21 = (1-cos(ang_rad))*ry*rx + sin(ang_rad)*rz
        var a22 = cos(ang_rad) + (1-cos(ang_rad))*ry*ry
        var a23 = (1-cos(ang_rad))*ry*rz - sin(ang_rad)*rx

        var a31 = (1-cos(ang_rad))*rz*rx - sin(ang_rad)*ry
        var a32 = (1-cos(ang_rad))*rz*ry + sin(ang_rad)*rx
        var a33 = cos(ang_rad) + (1-cos(ang_rad))*rz*rz

        var x = point[0]*a11 + point[1]*a21 + point[2]*a31
        var y = point[0]*a12 + point[1]*a22 + point[2]*a32
        var z = point[0]*a13 + point[1]*a23 + point[2]*a33
        return floatArrayOf(x,y,z)
    }

    fun calculateEyeVector(alfa:Float, beta:Float, gama:Float):FloatArray{
        var a = alfa/180 * PI
        var b = beta/180 * PI
        var c = gama/180 * PI

        //var k = rotateByY(b.toFloat(), floatArrayOf(0f,0f,1f))
        var k = rotateByVector(-a.toFloat(), floatArrayOf(1f,0f,0f), floatArrayOf(0f,0f,1f))
        k = rotateByVector(b.toFloat(), k, floatArrayOf(0f,1f,0f))
        
        //k = rotateByX(b.toFloat(), k)
        var s = sqrt(k[0]*k[0] + k[1]*k[1] + k[2]*k[2])
        k[0] = k[0]/s
        k[1] = k[1]/s
        k[2] = k[2]/s
        return k
    }
    private fun getVertexData(row: Int, col: Int): FloatArray {
        val inc = Math.PI * 2 / Math.pow(2.0, zoom.toDouble())
        val theta = row * inc - Math.PI
        //phi = col * inc - Math.PI;
        val phi = col * inc / 2


        //float color = (float) ((theta + Math.PI) / (2 * Math.PI));
        val color =
            (Math.abs(theta / Math.PI) / 2 + Math.abs((phi - Math.PI / 2) / (Math.PI / 2)) / 2).toFloat()
        val spherePoints = arrayOf(
            doubleArrayOf(theta, phi),
            doubleArrayOf(theta + inc, phi),
            doubleArrayOf(theta + inc, phi + inc / 2),
            doubleArrayOf(theta, phi + inc / 2)
        )
        val vertexPoints =
            Array(4) { DoubleArray(3) }
        for (i in spherePoints.indices) vertexPoints[i] = sphereToXYZ(spherePoints[i])
        return floatArrayOf( //Tri1
            vertexPoints[0][0].toFloat(),
            vertexPoints[0][1].toFloat(),
            vertexPoints[0][2].toFloat(),
            color,
            color,
            color,
            1.0f,
            vertexPoints[1][0].toFloat(),
            vertexPoints[1][1].toFloat(),
            vertexPoints[1][2].toFloat(),
            color,
            color,
            color,
            1.0f,
            vertexPoints[3][0].toFloat(),
            vertexPoints[3][1].toFloat(),
            vertexPoints[3][2].toFloat(),
            color,
            color,
            color,
            1.0f,  //tri2
            vertexPoints[3][0].toFloat(),
            vertexPoints[3][1].toFloat(),
            vertexPoints[3][2].toFloat(),
            color,
            color,
            color,
            1.0f,
            vertexPoints[1][0].toFloat(),
            vertexPoints[1][1].toFloat(),
            vertexPoints[1][2].toFloat(),
            color,
            color,
            color,
            1.0f,
            vertexPoints[2][0].toFloat(),
            vertexPoints[2][1].toFloat(),
            vertexPoints[2][2].toFloat(),
            color,
            color,
            color,
            1.0f
        )
    }

    private fun sphereToXYZ(p: DoubleArray): DoubleArray {
        if (p.size != 2) Log.e("DEBUG", "you dun goofed")
        return doubleArrayOf(
            Math.cos(p[0]) * Math.sin(p[1]),
            Math.sin(p[0]) * Math.sin(p[1]),
            Math.cos(p[1])
        )
    }// Bind the vertex shader to the program.

    // Bind the fragment shader to the program.

    // Bind attributes

    // Link the two shaders together into a program.

    // Get the link status.

    // If the link failed, delete the program.
// Pass in the shader source.

    // Compile the shader.

    // Get the compilation status.

    // If the compilation failed, delete the shader.

    // Create a program object and store the handle to it.
// Pass in the shader source.

    // Compile the shader.

    // Get the compilation status.

    // If the compilation failed, delete the shader.


    // Load in the fragment shader shader.
// Set the default precision to medium. We don't need as high of a
    // precision in the fragment shader.
    // This is the color from the vertex shader interpolated across the
    // triangle per fragment.
    // The entry point for our fragment shader.
    // Pass the color directly through the pipeline.

    // A constant representing the combined model/view/projection matrix.
    // Per-vertex position information we will pass in.
    // Per-vertex color information we will pass in.
    // This will be passed into the fragment shader.
    // The entry point for our vertex shader.
    // Pass the color through to the fragment shader.
    // It will be interpolated across the triangle.
    // gl_Position is a special variable used to store the final position.
    // Multiply the vertex by the matrix to get the final point in
    // normalized screen coordinates.
    val program: Int
        get() {
            val vertexShader =
                """uniform mat4 u_MVPMatrix;      
attribute vec4 a_Position;     
attribute vec4 a_Color;        
varying vec4 v_Color;          
void main()                    
{                              
   v_Color = a_Color;          
   gl_Position = u_MVPMatrix   
               * a_Position;   
}                              
""" // normalized screen coordinates.
            val fragmentShader =
                """precision mediump float;       
varying vec4 v_Color;          
void main()                    
{                              
   gl_FragColor = v_Color;     
}                              
"""
            var vertexShaderHandle = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER)
            if (vertexShaderHandle != 0) {
                // Pass in the shader source.
                GLES20.glShaderSource(vertexShaderHandle, vertexShader)

                // Compile the shader.
                GLES20.glCompileShader(vertexShaderHandle)

                // Get the compilation status.
                val compileStatus = IntArray(1)
                GLES20.glGetShaderiv(vertexShaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

                // If the compilation failed, delete the shader.
                if (compileStatus[0] == 0) {
                    GLES20.glDeleteShader(vertexShaderHandle)
                    vertexShaderHandle = 0
                }
            }
            if (vertexShaderHandle == 0) {
                throw RuntimeException("Error creating vertex shader.")
            }


            // Load in the fragment shader shader.
            var fragmentShaderHandle = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER)
            if (fragmentShaderHandle != 0) {
                // Pass in the shader source.
                GLES20.glShaderSource(fragmentShaderHandle, fragmentShader)

                // Compile the shader.
                GLES20.glCompileShader(fragmentShaderHandle)

                // Get the compilation status.
                val compileStatus = IntArray(1)
                GLES20.glGetShaderiv(
                    fragmentShaderHandle,
                    GLES20.GL_COMPILE_STATUS,
                    compileStatus,
                    0
                )

                // If the compilation failed, delete the shader.
                if (compileStatus[0] == 0) {
                    GLES20.glDeleteShader(fragmentShaderHandle)
                    fragmentShaderHandle = 0
                }
            }
            if (fragmentShaderHandle == 0) {
                throw RuntimeException("Error creating fragment shader.")
            }

            // Create a program object and store the handle to it.
            var programHandle = GLES20.glCreateProgram()
            if (programHandle != 0) {
                // Bind the vertex shader to the program.
                GLES20.glAttachShader(programHandle, vertexShaderHandle)

                // Bind the fragment shader to the program.
                GLES20.glAttachShader(programHandle, fragmentShaderHandle)

                // Bind attributes
                GLES20.glBindAttribLocation(programHandle, 0, "a_Position")
                GLES20.glBindAttribLocation(programHandle, 1, "a_Color")

                // Link the two shaders together into a program.
                GLES20.glLinkProgram(programHandle)

                // Get the link status.
                val linkStatus = IntArray(1)
                GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0)

                // If the link failed, delete the program.
                if (linkStatus[0] == 0) {
                    GLES20.glDeleteProgram(programHandle)
                    programHandle = 0
                }
            }
            if (programHandle == 0) {
                throw RuntimeException("Error creating program.")
            }
            return programHandle
        }

}