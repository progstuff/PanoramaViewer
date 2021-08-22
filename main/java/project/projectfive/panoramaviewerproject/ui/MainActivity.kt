package project.projectfive.panoramaviewerproject.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import project.projectfive.panoramaviewerproject.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }
    }

}
