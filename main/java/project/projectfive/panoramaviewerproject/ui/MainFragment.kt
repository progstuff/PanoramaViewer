package project.projectfive.panoramaviewerproject.ui

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import project.projectfive.panoramaviewerproject.GyroClasses.GyroData
import project.projectfive.panoramaviewerproject.GyroClasses.GyroscopeManager
import project.projectfive.panoramaviewerproject.OpenGLClasses.MyGlSurfaceView
import project.projectfive.panoramaviewerproject.R
import project.projectfive.panoramaviewerproject.ViewModels.MainViewModel


class MainFragment : Fragment() {

    companion object {
        fun newInstance() =
            MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var textX:TextView
    private lateinit var textY:TextView
    private lateinit var textZ:TextView
    private lateinit var textiX:TextView
    private lateinit var textiY:TextView
    private lateinit var textiZ:TextView
    private lateinit var calibrateButton:Button
    private lateinit var glSurfaceView: MyGlSurfaceView
    private lateinit var gyroscopeManager: GyroscopeManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflatedView: View = inflater.inflate(R.layout.main_fragment, container, false)
        gyroscopeManager =
            GyroscopeManager(
                requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            )

        textX = inflatedView.findViewById(R.id.textX)
        textY = inflatedView.findViewById(R.id.textY)
        textZ = inflatedView.findViewById(R.id.textZ)
        textiX = inflatedView.findViewById(R.id.iX)
        textiY = inflatedView.findViewById(R.id.iY)
        textiZ = inflatedView.findViewById(R.id.iZ)
        calibrateButton = inflatedView.findViewById(R.id.btn_calibration)
        calibrateButton.setOnClickListener {
            gyroscopeManager.isCalibrationState = true
            calibrateButton.isEnabled = false
        }
        Log.d("ACCEL","inflated")

        var layout:LinearLayout = inflatedView.findViewById(R.id.surface_layout)

        glSurfaceView =
            MyGlSurfaceView(
                context
            )
        context?.let{
            layout.addView(glSurfaceView)
        }
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getGyroData().observe(this, Observer<GyroData>{ gyroData ->
            textX.text = "X : ${gyroData.x} rad/s"
            textY.text = "Y : ${gyroData.y} rad/s"
            textZ.text = "Z : ${gyroData.z} rad/s"

            textiX.text = "X : ${gyroData.iX} grad"
            textiY.text = "Y : ${gyroData.iY} grad"
            textiZ.text = "Z : ${gyroData.iZ} grad"

            glSurfaceView.setAngles(gyroData.iX, gyroData.iY, gyroData.iZ)
            calibrateButton.isEnabled = !gyroscopeManager.isCalibrationState
        })

        gyroscopeManager.setViewModel(this)
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
        gyroscopeManager.sensorManager.unregisterListener(gyroscopeManager);
    }



}
