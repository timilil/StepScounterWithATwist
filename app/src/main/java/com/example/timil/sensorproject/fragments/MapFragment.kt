package com.example.timil.sensorproject.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
    private var activityCallBack: MapFragmentTrophyClickListener? = null

    interface MapFragmentTrophyClickListener {
        fun onTrophyClick(id: Long, latitude: Double, longitude: Double)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // instantiate the trophy click listener
        activityCallBack = context as MapFragmentTrophyClickListener

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)

        val ctx = context

        //set the user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))

        return inflater.inflate(R.layout.map_fragment, container, false)
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = TrophyDB.get(context!!)

        // create the map
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(15.0)

        // get the last location using the Google API client listener
        fusedLocationClient.lastLocation.addOnCompleteListener(activity!!) {
            task ->
            if (task.isSuccessful && task.result != null) {

                // this condition was needed, because without it the app would crash if user navigates out of map fragment before the map is created
                if (map != null) {
                    map.controller.setCenter(GeoPoint(task.result.latitude, task.result.longitude))

                    val myLocation = MyLocationNewOverlay(map)
                    myLocation.enableMyLocation()
                    myLocation.enableFollowLocation()
                    // add overlay to user's current location --> this will be updated when the user moves
                    map.overlays.add(myLocation)

                    doAsync {
                        // if the condition is true --> add trophies to trophy location DB so that there is always 11 trophies
                        if(db.trophyDao().getAllOld().size < 11){
                            val addItemsToDbCount = 10-db.trophyDao().getAllOld().size
                            for (i in 0..addItemsToDbCount){
                                // trophy range between 1000 and 8000 meters
                                val resultRange = randomTrophyRange(1000, 8000)
                                addTrophyWithRandomLocationToDb(task.result.longitude, task.result.latitude, resultRange)
                            }
                        }
                        else {
                            for (i in 0 until db.trophyDao().getAllOld().size){
                                // if the distance to trophy is more than 8000 meters --> delete it and add a new one
                                // this is because if the user for example travels to a different country, the trophies are relocated base on his current location
                                val distanceInMeters = getDistanceToTrophy(task.result.latitude, task.result.longitude, db.trophyDao().getAllOld()[i].latitude, db.trophyDao().getAllOld()[i].longitude )
                                if (distanceInMeters > 8000) {
                                    db.trophyDao().delete(db.trophyDao().getAllOld()[i])
                                    val resultRange = randomTrophyRange(1000, 8000)
                                    addTrophyWithRandomLocationToDb(task.result.longitude, task.result.latitude, resultRange)
                                }
                            }
                        }
                        UI {
                            // add the trophy markers with drawable image to an overlayItem array
                            for (trophy in db.trophyDao().getAllOld()) {
                                val olItem = OverlayItem(trophy.trophyid.toString(), null, GeoPoint(trophy.latitude, trophy.longitude))
                                val newMarker = resources.getDrawable(R.drawable.trophy, null)
                                olItem.setMarker(newMarker)

                                items.add(olItem)
                            }
                            // set click/tap listener and for each of the items in the array
                            val mOverlay = ItemizedOverlayWithFocus<OverlayItem>(context,items,
                                    object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                                        override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                                            return true
                                        }

                                        override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                                            //if user is close to this item and clicks the trophy on map: add the ar view to collect the prize
                                            val distanceInMeters = getDistanceToTrophy(task.result.latitude, task.result.longitude, item.point.latitude, item.point.longitude )
                                            if(distanceInMeters < 150.0){
                                                activityCallBack!!.onTrophyClick(item.title.toLong(), item.point.latitude, item.point.longitude)
                                            } else {
                                                Toast.makeText(context, "Get closer to the target, your distance must be less than 150 m (distance is "+distanceInMeters.toInt()+" meters)", Toast.LENGTH_SHORT).show()
                                            }
                                            return true
                                        }
                                    })
                            // finally populate the map
                            map.overlays.add(mOverlay)

                        }
                    }
                }
            } else Toast.makeText(context, "Location not provided", Toast.LENGTH_SHORT).show()
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

        val db = TrophyDB.get(context!!)
        db.trophyDao().insert(Trophy(0, foundLatitude, foundLongitude))
    }

    private fun randomTrophyRange(minRange: Int, maxRange: Int): Int {
        val radius = Random()
        return radius.nextInt(maxRange - minRange) + minRange
    }

    private fun getDistanceToTrophy(myLocationLat: Double, myLocationLong: Double, trophyLocationLat: Double, trophyLocationLong: Double ) : Float {
        val myCurrentLocation = Location("")
        myCurrentLocation.latitude = myLocationLat
        myCurrentLocation.longitude = myLocationLong

        val clickedMarkerLocation = Location("")
        clickedMarkerLocation.latitude = trophyLocationLat
        clickedMarkerLocation.longitude = trophyLocationLong

        return myCurrentLocation.distanceTo(clickedMarkerLocation)
    }

}