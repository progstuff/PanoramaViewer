package project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent


class MyGLExampleSurfaceView(context: Context?) : GLSurfaceView(context) {
    private val mRenderer: MyGLExampleRenderer
    private val TOUCH_SCALE_FACTOR = 180.0f / 320
    private var mPreviousX = 0f
    private var mPreviousY = 0f
    private var isOutView = true
    override fun onTouchEvent(e: MotionEvent): Boolean {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        val x = e.x
        val y = e.y
        when (e.action) {
            MotionEvent.ACTION_MOVE -> {
                var dx = x - mPreviousX
                var dy = y - mPreviousY

                // reverse direction of rotation above the mid-line
                if (y > height / 2) {
                    dx = dx * -1
                }

                // reverse direction of rotation to left of the mid-line
                if (x < width / 2) {
                    dy = dy * -1
                }
                mRenderer.angle = mRenderer.angle +
                        (dx + dy) * TOUCH_SCALE_FACTOR // = 180.0f / 320
                requestRender()
            }
        }
        mPreviousX = x
        mPreviousY = y
        return true
    }

    fun setAngles(xAng:Float, yAng:Float, zAng:Float){
        mRenderer.xAngle = xAng
        mRenderer.yAngle = yAng
        mRenderer.zAngle = zAng
    }

    fun changePerspective(){
        if(isOutView){
            mRenderer.zm = 10f
        } else {
            mRenderer.zm = 1f
        }
        isOutView = !isOutView
    }

    init {

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2)
        //fix for error No Config chosen, but I don't know what this does.
        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer =
            MyGLExampleRenderer()
        setRenderer(mRenderer)

        // Render the view only when there is a change in the drawing data
        //renderMode = RENDERMODE_WHEN_DIRTY
    }
}