package project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log

import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class MyGLExampleRenderer : GLSurfaceView.Renderer {
    lateinit private var mTriangle: Triangle
    lateinit private var mSquare: Square
    lateinit private var mCube: Cube
    lateinit var mCubes:ArrayList<Cube>
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    private val mRotationMatrix = FloatArray(16)
    var xAngle = 0.0f
    var yAngle = 0.0f
    var zAngle = 0.0f
    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    var angle = 0f

    override fun onSurfaceCreated(unused: GL10?, config: EGLConfig?) {

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        mTriangle = Triangle()
        mSquare = Square()
        mCubes = ArrayList()
        var startZ:Float = -1f
        var startY:Float = -1f
        for (i in 1..20){
            for (j in 1..20) {
                mCubes.add(Cube(0.02f, 0f, startY + 0.1f * j.toFloat(), startZ + 0.1f * i.toFloat()))
            }
        }
        //mCube = Cube(0.01f, 0f, 0f, 0f)
    }

    override fun onDrawFrame(unused: GL10) {
        val scratch = FloatArray(16)

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Set the camera position (View matrix)
        //Matrix.setLookAtM(mViewMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        val ang = calculateEyeVector(xAngle, yAngle)
        Matrix.setLookAtM(mViewMatrix, 0, ang[0]*5f, ang[1]*5f, ang[2]*5f, 0f, 0f, 0f, 0f, 1f, 0f)

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0)

        // Draw square
        //mSquare.draw(mMVPMatrix)
        for(i in 1..mCubes.size){
            mCubes.get(i-1).draw(mMVPMatrix)
        }
        //mCube.draw(mMVPMatrix)
        // Create a rotation for the triangle

        // Use the following code to generate constant rotation.
        // Leave this code out when using TouchEvents.
        // long time = SystemClock.uptimeMillis() % 4000L;
        // float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0f, 0f, 1.0f)

        // Combine the rotation matrix with the projection and camera view
        // Note that the mMVPMatrix factor *must be first* in order
        // for the matrix multiplication product to be correct.
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0)

        // Draw triangle
        //mTriangle.draw(scratch)

        //Log.d("ANGLES",xAngle.toString())
    }

    fun rotateByVector(ang_rad:Float, point:FloatArray, rotateVector:FloatArray):FloatArray{
        var rx = rotateVector[0]
        var ry = rotateVector[1]
        var rz = rotateVector[2]
        var a11 = cos(ang_rad) + (1- cos(ang_rad))*rx*rx
        var a12 = (1- cos(ang_rad))*rx*ry - sin(ang_rad) *rz
        var a13 = (1- cos(ang_rad))*rx*rz + sin(ang_rad) *ry

        var a21 = (1- cos(ang_rad))*ry*rx + sin(ang_rad) *rz
        var a22 = cos(ang_rad) + (1- cos(ang_rad))*ry*ry
        var a23 = (1- cos(ang_rad))*ry*rz - sin(ang_rad) *rx

        var a31 = (1- cos(ang_rad))*rz*rx - sin(ang_rad) *ry
        var a32 = (1- cos(ang_rad))*rz*ry + sin(ang_rad) *rx
        var a33 = cos(ang_rad) + (1- cos(ang_rad))*rz*rz

        var x = point[0]*a11 + point[1]*a21 + point[2]*a31
        var y = point[0]*a12 + point[1]*a22 + point[2]*a32
        var z = point[0]*a13 + point[1]*a23 + point[2]*a33
        return floatArrayOf(x,y,z)
    }

    fun calculateEyeVector(alfa:Float, beta:Float):FloatArray{
        var a = alfa/180 * PI
        var b = beta/180 * PI

        var k = rotateByVector(-a.toFloat(), floatArrayOf(1f,0f,0f), floatArrayOf(0f,0f,1f))
        k = rotateByVector(b.toFloat(), k, floatArrayOf(0f,1f,0f))

        var s = sqrt(k[0]*k[0] + k[1]*k[1] + k[2]*k[2])
        k[0] = k[0]/s
        k[1] = k[1]/s
        k[2] = k[2]/s
        return k
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    companion object {
        private const val TAG = "MyGLRenderer"

        /**
         * Utility method for compiling a OpenGL shader.
         *
         *
         * **Note:** When developing shaders, use the checkGlError()
         * method to debug shader coding errors.
         *
         * @param type - Vertex or fragment shader type.
         * @param shaderCode - String containing the shader code.
         * @return - Returns an id for the shader.
         */
        fun loadShader(type: Int, shaderCode: String?): Int {

            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            val shader = GLES20.glCreateShader(type)

            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode)
            GLES20.glCompileShader(shader)
            return shader
        }

        /**
         * Utility method for debugging OpenGL calls. Provide the name of the call
         * just after making it:
         *
         * <pre>
         * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
         * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
         *
         * If the operation is not successful, the check throws an error.
         *
         * @param glOperation - Name of the OpenGL call to check.
         */
        fun checkGlError(glOperation: String) {
            var error: Int
            while (GLES20.glGetError().also { error = it } != GLES20.GL_NO_ERROR) {
                Log.e(TAG, "$glOperation: glError $error")
                throw RuntimeException("$glOperation: glError $error")
            }
        }
    }
}