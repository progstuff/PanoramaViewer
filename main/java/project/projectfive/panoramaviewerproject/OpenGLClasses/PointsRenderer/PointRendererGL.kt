package project.projectfive.panoramaviewerproject.OpenGLClasses.PointsRenderer
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLU
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


class PointRendererGL (private var zoom: Int) : GLSurfaceView.Renderer {
    private var programHandle = 0
    private val mModelMatrix = FloatArray(16)
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mViewMatrix = FloatArray(16)
    var xAngle = 0.0f
    var yAngle = 0.0f
    var zAngle = 0.0f

    /** This will be used to pass in the transformation matrix.  */
    private var mMVPMatrixHandle = 0



    override fun onSurfaceCreated(g1: GL10?, config: javax.microedition.khronos.egl.EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.5f, 0.5f, 1.0f)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glDepthMask(true)

    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        /*GLES20.glViewport(0, 0, width, height)

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        val ratio = width.toFloat() / height
        val left = -ratio
        val bottom = -1.0f
        val top = 1.0f
        val near = 1.0f
        val far = 10.0f
        Matrix.frustumM(mProjectionMatrix, 0, left, ratio, bottom, top, near, far)*/
        gl.apply {
            glViewport(0, 0, width, height)

            // make adjustments for screen ratio
            val ratio: Float = width.toFloat() / height.toFloat()

            glMatrixMode(GL10.GL_PROJECTION)            // set matrix to projection mode
            glLoadIdentity()                            // reset the matrix to its default state
            glFrustumf(-ratio, ratio, -1f, 1f, 3f, 7f)  // apply the projection matrix
        }
    }

    private val vertexShaderCode =
    // This matrix member variable provides a hook to manipulate
        // the coordinates of objects that use this vertex shader.
        "uniform mat4 uMVPMatrix;   \n" +
                "attribute vec4 vPosition;  \n" +
                "void main(){               \n" +
                // The matrix must be included as part of gl_Position
                // Note that the uMVPMatrix factor *must be first* in order
                // for the matrix multiplication product to be correct.
                " gl_Position = uMVPMatrix * vPosition; \n" +
                "}  \n"

    var fragmentShaderCode:String =
        "precision medium float;"+
                "uniform vec4 vColor;"+
                "void main() {"+
                "   gl_FragColor = vColor;" +
                "}"
    override fun onDrawFrame(gl: GL10) {

        gl.apply {
            // Set GL_MODELVIEW transformation mode
            glMatrixMode(GL10.GL_MODELVIEW)
            glLoadIdentity()                     // reset the matrix to its default state
        }

        // When using GL_MODELVIEW, you must set the camera view
        GLU.gluLookAt(gl, 0f, 0f, -5f, 0f, 0f, 0f, 0f, 1.0f, 0.0f)

        /*GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT or GLES20.GL_COLOR_BUFFER_BIT)
        Matrix.setIdentityM(mModelMatrix, 0)
        val time = SystemClock.uptimeMillis() % 10000L
        val angleInDegrees = 360.0f / 5000.0f * time.toInt()

        // Draw the triangle facing straight on.
        Matrix.setIdentityM(mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0)
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0)
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0)

        GLES20.glDrawArrays(
            GLES20.GL_TRIANGLES,
            0,
            6 * Math.pow(4.0, zoom.toDouble()).toInt()
        )

        val ang = calculateEyeVector(xAngle, yAngle)
        Matrix.setLookAtM(mViewMatrix, 0, ang[0]*5f, ang[1]*5f, ang[2]*5f, 0f, 0f, 0f, 0f, 1f, 0f)*/



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

}