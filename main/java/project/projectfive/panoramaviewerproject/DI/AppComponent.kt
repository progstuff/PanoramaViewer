package project.projectfive.panoramaviewerproject.DI

import dagger.Component
import project.projectfive.panoramaviewerproject.GyroClasses.GyroData
import project.projectfive.panoramaviewerproject.GyroClasses.GyroscopeManager
import javax.inject.Singleton

@Singleton
@Component(modules = [GyroData::class])
interface AppComponent {
    fun inject(gyroscopeManager: GyroscopeManager)
}