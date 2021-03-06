package project.projectfive.panoramaviewerproject.ui

import android.content.Context
import android.graphics.Point
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.view.PreviewView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import project.projectfive.panoramaviewerproject.CameraClasses.MyCameraManager
import project.projectfive.panoramaviewerproject.GyroClasses.GyroData
import project.projectfive.panoramaviewerproject.GyroClasses.GyroscopeManager
import project.projectfive.panoramaviewerproject.OpenGLClasses.CubeRenderer.MyGlSurfaceView
import project.projectfive.panoramaviewerproject.OpenGLClasses.OpenGLExamples.MyGLExampleSurfaceView
import project.projectfive.panoramaviewerproject.OpenGLClasses.PointsRenderer.PointSurfaceViewGL
import project.projectfive.panoramaviewerproject.R
import project.projectfive.panoramaviewerproject.ViewModels.MainViewModel
import project.projectfive.panoramaviewerproject.OpenGLClasses.SphereRenderer.SphereSurfaceViewGL


class MainFragment : Fragment() {

    companion object {
        fun newInstance() =
            MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var cameraButton:Button
    private lateinit var calibrateButton:Button
    private lateinit var perspectiveButton:Button
    private lateinit var glExampleView: MyGLExampleSurfaceView

    private lateinit var gyroscopeManager: GyroscopeManager
    private lateinit var cameraManager:MyCameraManager
    private lateinit var previewView:PreviewView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflatedView: View = inflater.inflate(R.layout.main_fragment, container, false)
        gyroscopeManager =
            GyroscopeManager(
                requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            )

        //textX = inflatedView.findViewById(R.id.textX)
        //textY = inflatedView.findViewById(R.id.textY)
        //textZ = inflatedView.findViewById(R.id.textZ)
        //textiX = inflatedView.findViewById(R.id.iX)
        //textiY = inflatedView.findViewById(R.id.iY)
        //textiZ = inflatedView.findViewById(R.id.iZ)

        calibrateButton = inflatedView.findViewById(R.id.btn_calibration)
        calibrateButton.setOnClickListener {
            gyroscopeManager.isCalibrationState = true
            calibrateButton.isEnabled = false
        }
        perspectiveButton = inflatedView.findViewById(R.id.perspective_change)
        perspectiveButton.setOnClickListener {
            glExampleView.changePerspective()
        }
        Log.d("ACCEL","inflated")

        var layout:LinearLayout = inflatedView.findViewById(R.id.surface_layout)

        val display: Display? = activity?.getWindowManager()?.getDefaultDisplay()
        val size = Point()
        display?.getSize(size)
        glExampleView = MyGLExampleSurfaceView(context)
        context?.let{
            layout.addView(glExampleView )
        }



        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getGyroData().observe(this, Observer<GyroData>{ gyroData ->
            //textX.text = "X : ${gyroData.x} rad/s"
            //textY.text = "Y : ${gyroData.y} rad/s"
            //textZ.text = "Z : ${gyroData.z} rad/s"

            //textiX.text = "X : ${gyroData.iX} grad"
            //textiY.text = "Y : ${gyroData.iY} grad"
            //textiZ.text = "Z : ${gyroData.iZ} grad"

            glExampleView.setAngles(gyroData.iX, gyroData.iY, gyroData.iZ)
            calibrateButton.isEnabled = !gyroscopeManager.isCalibrationState
        })

        gyroscopeManager.setViewModel(this)

        cameraButton = inflatedView.findViewById(R.id.camera_button)
        previewView = inflatedView.findViewById(R.id.viewFinder)
        cameraManager = MyCameraManager(activity as AppCompatActivity, cameraButton, previewView)
        return inflatedView
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

    }

    override fun onResume() {
        super.onResume()
        gyroscopeManager.sensorManager.registerListener(gyroscopeManager, gyroscopeManager.sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    override fun onStop() {
        super.onStop()
        gyroscopeManager.sensorManager.unregisterListener(gyroscopeManager)
        cameraManager.takeOff()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        cameraManager.tryToStartCamera(activity as AppCompatActivity, requestCode, previewView)
    }



}
