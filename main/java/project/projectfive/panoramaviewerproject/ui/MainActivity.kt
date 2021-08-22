package project.projectfive.panoramaviewerproject.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val fragments: List<Fragment> =
            supportFragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

}
