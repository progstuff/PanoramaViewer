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


class MainFragment : Fragment(), SensorEventListener {

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
    private lateinit var sensorManager:SensorManager
    private lateinit var sensor: Sensor

    private var gyroTimestamp1: Long = 0L
    private var gyroTimestamp2: Long = 0L
    private var gyroDt:Long = 500000
    private var iX:Float = 0.0f
    private var iY:Float = 0.0f
    private var iZ:Float = 0.0f
    private var isCalibrationState:Boolean = false
    private var calibrationDone:Boolean = false
    private var sigmaX:Float = 0.0f
    private var measuresX:ArrayList<Float> = ArrayList()
    private var sigmaY:Float = 0.0f
    private var measuresY:ArrayList<Float> = ArrayList()
    private var sigmaZ:Float = 0.0f
    private var measuresZ:ArrayList<Float> = ArrayList()
    private var currentCalibrationDt: Float = 0.0f
    private var calibrationDt:Float = 5.0f

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
        textiX = inflatedView.findViewById(R.id.iX)
        textiY = inflatedView.findViewById(R.id.iY)
        textiZ = inflatedView.findViewById(R.id.iZ)
        calibrateButton = inflatedView.findViewById(R.id.btn_calibration)
        calibrateButton.setOnClickListener {
            isCalibrationState = true
            calibrateButton.isEnabled = false
        }
        Log.d("ACCEL","inflated")

        var layout:LinearLayout = inflatedView.findViewById(R.id.surface_layout)

        context?.let{
            layout.addView(MyGlSurfaceView(context))

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
        sensorManager.unregisterListener(this);
    }
    override fun onSensorChanged(event: SensorEvent?) {
        val data:FloatArray = event?.values ?: FloatArray(3)
        val x = data[0]/ PI*180
        val y = data[1]/ PI*180
        val z = data[2]/ PI*180
        textX.text = "X : $x rad/s"
        textY.text = "Y : $y rad/s"
        textZ.text = "Z : $z rad/s"
        if(gyroTimestamp1 == 0L){
            gyroTimestamp1 = event?.timestamp ?: gyroTimestamp1
        }
        gyroTimestamp2 = event?.timestamp ?: gyroTimestamp2
        if(gyroTimestamp2 - gyroTimestamp1 > gyroDt){

            val dt:Float = (gyroTimestamp2 - gyroTimestamp1).toFloat()/1000000000.0f
            val dx = (x*dt).toFloat()
            val dy = (y*dt).toFloat()
            val dz = (z*dt).toFloat()
            if(abs(dx) > sigmaX) {
                iX += (x * dt).toFloat()
            }
            if(abs(dy) > sigmaY) {
                iY += (y * dt).toFloat()
            }
            if(abs(dz) > sigmaZ) {
                iZ += (z*dt).toFloat()
            }
            gyroTimestamp1 = event?.timestamp ?: gyroTimestamp1

            saveCalibrationData(x.toFloat(), y.toFloat(), z.toFloat(), dt)
        }

        textiX.text = "X : $iX grad"
        textiY.text = "Y : $iY grad"
        textiZ.text = "Z : $iZ grad"


        //Log.d("ACCEL","data changed")
    }

    fun saveCalibrationData(x:Float, y:Float, z:Float, dt:Float){
        if(isCalibrationState && !calibrationDone) {

            measuresX.add(x / dt)
            measuresY.add(y / dt)
            measuresZ.add(z / dt)

            currentCalibrationDt += dt
            if(currentCalibrationDt >= calibrationDt){
                calibrationDone = true
                ///
                val meanX = measuresX.average().toFloat()
                val meanY = measuresY.average().toFloat()
                val meanZ = measuresZ.average().toFloat()
                sigmaX = calculateSD(measuresX, meanX)
                sigmaY = calculateSD(measuresY, meanY)
                sigmaZ = calculateSD(measuresZ, meanZ)
                currentCalibrationDt = 0.0f
                measuresX.clear()
                measuresY.clear()
                measuresZ.clear()
                iX = 0.0f
                iY = 0.0f
                iZ = 0.0f
                calibrateButton.isEnabled = true
                isCalibrationState = false
            }
        } else if(!isCalibrationState){
            calibrationDone = false
        }
    }

    fun calculateSD(data:ArrayList<Float>, mean:Float):Float{
        var sum:Float = 0.0f
        for (i in 1..data.size){
            sum += (data.get(i-1) - mean).pow(2)
        }

        var sd:Float = (sum/data.size)
        return sqrt(sd)
    }


}
