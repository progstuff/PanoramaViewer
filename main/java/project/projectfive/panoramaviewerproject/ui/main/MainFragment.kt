package project.projectfive.panoramaviewerproject.ui.main

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import project.projectfive.panoramaviewerproject.R
import java.util.*
import kotlin.math.PI


class MainFragment : Fragment(), SensorEventListener {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var textX:TextView
    private lateinit var textY:TextView
    private lateinit var textZ:TextView
    private lateinit var sensorManager:SensorManager
    private lateinit var sensor: Sensor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val inflatedView: View = inflater.inflate(R.layout.main_fragment, container, false)
        this.sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        textX = inflatedView.findViewById(R.id.textX)
        textY = inflatedView.findViewById(R.id.textY)
        textZ = inflatedView.findViewById(R.id.textZ)
        Log.d("ACCEL","inflated")

        var layout:LinearLayout = inflatedView.findViewById(R.id.surface_layout)

        context?.let{
            layout.addView(MyGLSurfaceView(it))
        }


        return inflatedView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    override fun onStop() {
        super.onStop()
        //sensorManager.unregisterListener(this);
    }
    override fun onSensorChanged(event: SensorEvent?) {
        val data:FloatArray = event?.values ?: FloatArray(3)
        val x = data[0]/ PI*180
        val y = data[1]/ PI*180
        val z = data[2]/ PI*180
        textX.setText("X : $x rad/s");
        textY.setText("Y : $y rad/s");
        textZ.setText("Z : $z rad/s");
        //Log.d("ACCEL","data changed")
    }


}
