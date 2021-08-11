package project.projectfive.panoramaviewerproject.ui.main

import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import project.projectfive.panoramaviewerproject.R
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var textX:TextView
    private lateinit var textY:TextView
    private lateinit var textZ:TextView
    private lateinit var textiX:TextView
    private lateinit var textiY:TextView
    private lateinit var textiZ:TextView
    private lateinit var calibrateButton:Button
    private lateinit var glSurfaceView:MyGlSurfaceView
    private lateinit var gyroscopeManager: GyroscopeManager


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflatedView: View = inflater.inflate(R.layout.main_fragment, container, false)
        gyroscopeManager = GyroscopeManager(requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager)

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

        glSurfaceView = MyGlSurfaceView(context)
        context?.let{
            layout.addView(glSurfaceView)
        }

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
