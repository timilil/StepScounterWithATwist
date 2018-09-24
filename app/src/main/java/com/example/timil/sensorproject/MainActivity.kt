package com.example.timil.sensorproject

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : /*AppCompatActivity()*/ FragmentActivity() {

    private val homeFragment = HomeFragment()
    private val statisticsFragment = StatisticsFragment()
    private val mapFragment = MapFragment()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, statisticsFragment).commit()

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, mapFragment).commit()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        if ((Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission(this,
                                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),0
            )
        }

        setContentView(R.layout.activity_main)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //swsupportFragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment).commit()

        //supportFragmentManager.beginTransaction().hide(mapFragment).commit()

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment).commit()

    }
}
