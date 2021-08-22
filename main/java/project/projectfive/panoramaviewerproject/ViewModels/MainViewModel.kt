package project.projectfive.panoramaviewerproject.ViewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import project.projectfive.panoramaviewerproject.GyroClasses.GyroData

class MainViewModel : ViewModel() {
    private val gyroscopeData: MutableLiveData<GyroData> by lazy {
        MutableLiveData<GyroData>().also {
            MutableLiveData<GyroData>()
        }
    }

    fun setGyroData(gd: GyroData){
        gyroscopeData.value = gd
    }

    fun getGyroData(): LiveData<GyroData> {
        return gyroscopeData
    }

}
