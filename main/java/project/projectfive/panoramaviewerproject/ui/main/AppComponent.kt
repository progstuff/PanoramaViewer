package project.projectfive.panoramaviewerproject.ui.main

import dagger.Component
import project.projectfive.panoramaviewerproject.MainActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [GyroData::class])
interface AppComponent {
    fun inject(gyroscopeManager: GyroscopeManager)
}