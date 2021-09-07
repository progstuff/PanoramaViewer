package project.projectfive.panoramaviewerproject.OpenGLClasses.PointsRenderer

import project.projectfive.panoramaviewerproject.OpenGLClasses.SphereRenderer.SphereRendererGL

import android.content.Context
import android.graphics.Point
import android.opengl.GLSurfaceView
import android.view.MotionEvent

class PointSurfaceViewGL : GLSurfaceView {
    var zooming = false
    private var zoom: Int
    var wdth:Int = 0
    var hght:Int = 0
    private var renderer: PointRendererGL
    private var timeOfLastZoom: Long

    constructor(context: Context?) : super(context) {
        setEGLContextClientVersion(2)
        setEGLConfigChooser(true)
        zoom = 4

        renderer = PointRendererGL(zoom)
        setRenderer(renderer)
        timeOfLastZoom = System.currentTimeMillis()
    }

    constructor(context: Context?, width: Int, height: Int) : super(context) {
        setEGLContextClientVersion(2)
        setEGLConfigChooser(true)
        zoom = 3
        val size = Point()
        this.wdth = width
        this.hght = height
        renderer = PointRendererGL(zoom)
        // Set the renderer to our demo renderer, defined below.

        setRenderer(renderer)
        timeOfLastZoom = System.currentTimeMillis()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //return super.onTouchEvent(event);
        /*if (System.currentTimeMillis() - timeOfLastZoom > 250 && !zooming) {
            //onPause();
            if (event.y > height / 2 && zoom <= 1 || event.y <= height / 2 && zoom >= 9) return true
            queueEvent {
                zooming = true
                if (event.y > height / 2) zoom-- else zoom++

                //                    if (zoom < 1)
                //                        zoom = 1;
                //                    else if (zoom > 9)
                //                        zoom = 9;
                renderer.zoom(zoom)
                timeOfLastZoom = System.currentTimeMillis()
                zooming = false
            }

            //onResume();
        }*/
        return true
    }

    fun setAngles(xAng:Float, yAng:Float, zAng:Float){
        renderer.xAngle = xAng
        renderer.yAngle = yAng
        renderer.zAngle = zAng
    }
}