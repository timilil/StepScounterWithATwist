package com.example.timil.sensorproject

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import android.support.v7.app.AppCompatActivity
import com.example.timil.sensorproject.fragments.HomeFragment
import com.example.timil.sensorproject.fragments.MapFragment
import com.example.timil.sensorproject.fragments.SettingsFragment
import com.example.timil.sensorproject.fragments.StatisticsFragment


class MainActivity : AppCompatActivity() /*FragmentActivity()*/ {

    private val homeFragment = HomeFragment()
    private val statisticsFragment = StatisticsFragment()
    private val mapFragment = MapFragment()
    private var pref: SharedPreferences? = null
    private var listener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val preferences = getSharedPreferences("pref_settings", Context.MODE_PRIVATE)
        val useTheme = preferences.getString("pref_settings", "N/A")

        Log.d("DBG", "Using theme: "+useTheme)
        /*if(useTheme == "AppBlueTheme"){
            setTheme(R.style.AppBlueTheme)
        }*/
        when(useTheme){
            "AppTheme" -> setTheme(R.style.AppTheme)
            "AppBlueTheme" -> setTheme(R.style.AppBlueTheme)
            "AppGreenTheme" -> setTheme(R.style.AppGreenTheme)
            "AppRedTheme" -> setTheme(R.style.AppRedTheme)
            "N/A" -> setTheme(R.style.AppTheme)
        }

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

        pref = PreferenceManager.getDefaultSharedPreferences(this)
        //Log.d("DBG", pref!!.getString("pref_settings", "N/A"))

        listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            Log.d("DBG", prefs.getString(key, "N/A")+key)

            //setTheme(R.style.AppGreenTheme)
            //recreate()
            val editor = getSharedPreferences("pref_settings", Context.MODE_PRIVATE).edit()
            editor.putString(key, prefs.getString(key, "N/A"))
            editor.apply()

            val intent = intent
            finish()

            startActivity(intent)
        }
        pref!!.registerOnSharedPreferenceChangeListener(listener)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        //swsupportFragmentManager.beginTransaction().add(R.id.fragment_container, mapFragment).commit()

        //supportFragmentManager.beginTransaction().hide(mapFragment).commit()

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment).commit()

    }

    override fun onDestroy() {
        super.onDestroy()
        pref!!.unregisterOnSharedPreferenceChangeListener(listener)
    }

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater

        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.settings -> {
                //Log.d("DBG", "Clicked settings")
                val settingsFragment = SettingsFragment()
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, settingsFragment).addToBackStack(null).commit()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
