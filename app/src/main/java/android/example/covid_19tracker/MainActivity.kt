@file:Suppress("DEPRECATION")

package android.example.covid_19tracker

import android.content.Context
import android.media.Image
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_state_list.*


class MainActivity : AppCompatActivity() {
    lateinit var fragmentAbout: fragment_about
    lateinit var fragmentSymptom: fragment_symptom
    lateinit var fragmentHome: fragment_home
    lateinit var fragmentPrecaution: fragment_precaution
    lateinit var fragmentState_list: State_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

            val chipNavigationBar: ChipNavigationBar = findViewById(R.id.menu_bottom)
            chipNavigationBar.setItemSelected(R.id.home, true)
            fragmentHome = fragment_home()
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragmentHome)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()


            chipNavigationBar.setOnItemSelectedListener { id ->
                when (id) {
                    R.id.home -> {
                            fragmentHome = fragment_home()
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, fragmentHome)
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                .commit()

                        }

                    R.id.state -> {
                        fragmentState_list = State_list()
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragmentState_list)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()

                    }
                    R.id.precaution -> {
                        fragmentPrecaution = fragment_precaution()
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragmentPrecaution)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()

                    }
                    R.id.symptom -> {
                        fragmentSymptom = fragment_symptom()
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragmentSymptom)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()

                    }
                    R.id.about -> {
                        fragmentAbout = fragment_about()
                        supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, fragmentAbout)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            .commit()

                    }
                }
            }
            true

    }
}