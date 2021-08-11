package project.projectfive.panoramaviewerproject.ui.main

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class GyroscopeManager(sensorMngr: SensorManager): SensorEventListener {
    private var gyroTimestamp1: Long = 0L
    private var gyroTimestamp2: Long = 0L
    private var gyroDt:Long = 500000
    private var x:Float = 0.0f
    private var y:Float = 0.0f
    private var z:Float = 0.0f
    private var iX:Float = 0.0f
    private var iY:Float = 0.0f
    private var iZ:Float = 0.0f
    var isCalibrationState:Boolean = false
    private var calibrationDone:Boolean = false
    private var sigmaX:Float = 0.0f
    private var measuresX:ArrayList<Float> = ArrayList()
    private var sigmaY:Float = 0.0f
    private var measuresY:ArrayList<Float> = ArrayList()
    private var sigmaZ:Float = 0.0f
    private var measuresZ:ArrayList<Float> = ArrayList()
    private var currentCalibrationDt: Float = 0.0f
    private var calibrationDt:Float = 5.0f
    var sensorManager: SensorManager
    var sensor: Sensor
    init{
        sensorManager = sensorMngr
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        val data:FloatArray = event?.values ?: FloatArray(3)
        val x = data[0]/ PI *180
        val y = data[1]/ PI *180
        val z = data[2]/ PI *180
        this.x = x.toFloat()
        this.y = y.toFloat()
        this.z = z.toFloat()

        //textX.text = "X : $x rad/s"
        //textY.text = "Y : $y rad/s"
        //textZ.text = "Z : $z rad/s"
        if(gyroTimestamp1 == 0L){
            gyroTimestamp1 = event?.timestamp ?: gyroTimestamp1
        }
        gyroTimestamp2 = event?.timestamp ?: gyroTimestamp2
        if(gyroTimestamp2 - gyroTimestamp1 > gyroDt){

            val dt:Float = (gyroTimestamp2 - gyroTimestamp1).toFloat()/1000000000.0f
            val dx = (x/dt).toFloat()
            val dy = (y/dt).toFloat()
            val dz = (z/dt).toFloat()
            if(abs(dx) > sigmaX*9) {
                iX += (x * dt).toFloat()
            }
            if(abs(dy) > sigmaY*9) {
                iY += (y * dt).toFloat()
            }
            if(abs(dz) > sigmaZ*9) {
                iZ += (z*dt).toFloat()
            }
            gyroTimestamp1 = event?.timestamp ?: gyroTimestamp1

            saveCalibrationData(x.toFloat(), y.toFloat(), z.toFloat(), dt)
        }

        //textiX.text = "X : $iX grad"
        //textiY.text = "Y : $iY grad"
        //textiZ.text = "Z : $iZ grad"
        //glSurfaceView.setAngles(iX, iY, iZ)

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
                //calibrateButton.isEnabled = true
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