package com.example.timil.sensorproject.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timil.sensorproject.R
import com.example.timil.sensorproject.database.TrophyDB
import com.example.timil.sensorproject.entities.Trophy
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.map_fragment.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.support.v4.UI
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import java.util.*


class MapFragment: Fragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var items = ArrayList<OverlayItem>()

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }*/

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        val ctx = context

        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        Log.d("DBG", "created map")
        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = TrophyDB.get(context!!)

        map.setTileSource(TileSourceFactory.MAPNIK)
        //map.setBuiltInZoomControls(true)
        map.setMultiTouchControls(true)
        map.controller.setZoom(15.0)

        fusedLocationClient.lastLocation.addOnCompleteListener(activity!!) {
            task ->
            if (task.isSuccessful && task.result != null) {

                map.controller.setCenter(GeoPoint(task.result.latitude, task.result.longitude))

                val myLocation = MyLocationNewOverlay(map)
                myLocation.enableMyLocation()
                myLocation.enableFollowLocation()
                map.overlays.add(myLocation)

                doAsync {

                    Log.d("DBG", "size: "+db.trophyDao().getAllOld().size)
                    if(db.trophyDao().getAllOld().size < 10){
                        //Log.d("DBG", "left: "+(10-db.trophyDao().getAllOld().size))
                        val addItemsToDbCount = 10-db.trophyDao().getAllOld().size
                        for (i in 0..addItemsToDbCount){
                            val radius = Random()
                            val minRange = 1000
                            val maxRange = 8000
                            val result = radius.nextInt(maxRange - minRange) + minRange
                            addTrophyWithRandomLocationToDb(task.result.longitude, task.result.latitude, result)
                            //Log.d("DBG", "result: "+result)
                        }
                    }
                    UI {
                        // livedata maybe for later?
                        /*val tmp = ViewModelProviders.of(this@MapFragment).get(TrophyModel::class.java)
                        tmp.getTrophies().observe(this@MapFragment, Observer {
                            //e.g. using a recycler view adapter
                            //list.adapter = UserAdapter(it?.sortedBy { it.lastname }, context)
                            Log.d("DBG", "this is debug "+it?.sortedBy { it.latitude }.toString())
                        })*/
                        for (trophy in db.trophyDao().getAllOld()) {
                            val olItem = OverlayItem(null, null, GeoPoint(trophy.latitude, trophy.longitude))
                            val newMarker = resources.getDrawable(R.drawable.trophy, null)
                            olItem.setMarker(newMarker)
                            items.add(olItem)
                            //db.trophyDao().delete(trophy)
                        }
                        val mOverlay = ItemizedOverlayWithFocus<OverlayItem>(context,items,
                                object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                                    override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                                        Log.d("DBG", "Long press")
                                        return true
                                    }

                                    override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                                        Log.d("DBG", "Clicked "+item.point.latitude)

                                        //if user is close to this item/walked distance is more than something: add the ar view so collect price...
                                        val myCurrentLocation = Location("")
                                        myCurrentLocation.latitude = task.result.latitude
                                        myCurrentLocation.longitude = task.result.longitude

                                        val clickedMarkerLocation = Location("")
                                        clickedMarkerLocation.latitude = item.point.latitude
                                        clickedMarkerLocation.longitude = item.point.longitude

                                        val distanceInMeters = myCurrentLocation.distanceTo(clickedMarkerLocation)

                                        Log.d("DBG", "Distance is $distanceInMeters")

                                        return true
                                    }
                                })
                        // adds description and title popup if needed...
                        //mOverlay.setFocusItemsOnTap(true)

                        map.overlays.add(mOverlay)

                        Log.d("DBG", "UI Here")
                    }
                }

            } else Log.d("GEOLOCATION", "not working")
        }
    }

    private fun addTrophyWithRandomLocationToDb(x0: Double, y0: Double, radius: Int) {
        val random = Random()

        // Convert radius from meters to degrees
        val radiusInDegrees = (radius / 111000f).toDouble()

        val u = random.nextDouble()
        val v = random.nextDouble()
        val w = radiusInDegrees * Math.sqrt(u)
        val t = 2.0 * Math.PI * v
        val x = w * Math.cos(t)
        val y = w * Math.sin(t)

        // Adjust the x-coordinate for the shrinking of the east-west distances
        val newX = x / Math.cos(Math.toRadians(y0))

        val foundLongitude = newX + x0
        val foundLatitude = y + y0
        println("Longitude: $foundLongitude  Latitude: $foundLatitude")

        val db = TrophyDB.get(context!!)
        db.trophyDao().insert(Trophy(0, foundLatitude, foundLongitude))
    }
}