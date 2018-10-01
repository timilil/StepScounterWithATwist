package com.example.timil.sensorproject.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.util.Log
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
        activityCallBack = context as MapFragmentTrophyClickListener

    }

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
                    if(db.trophyDao().getAllOld().size < 11){
                        //Log.d("DBG", "left: "+(10-db.trophyDao().getAllOld().size))
                        val addItemsToDbCount = 10-db.trophyDao().getAllOld().size
                        for (i in 0..addItemsToDbCount){
                            val resultRange = randomTrophyRange(1000, 8000)
                            addTrophyWithRandomLocationToDb(task.result.longitude, task.result.latitude, resultRange)
                            //Log.d("DBG", "result is: "+addTrophyWithRandomLocationToDb(task.result.longitude, task.result.latitude, resultRange))
                        }
                    } else {
                        for (i in 0 until db.trophyDao().getAllOld().size){
                            Log.d("DBG", "here is "+db.trophyDao().getAllOld()[i].toString())

                            val distanceInMeters = getDistanceToTrophy(task.result.latitude, task.result.longitude, db.trophyDao().getAllOld()[i].latitude, db.trophyDao().getAllOld()[i].longitude )
                            if (distanceInMeters > 8000) {
                                Log.d("DBG", "distance is too large, spawning another one $distanceInMeters")
                                db.trophyDao().delete(db.trophyDao().getAllOld()[i])
                                val resultRange = randomTrophyRange(1000, 8000)
                                addTrophyWithRandomLocationToDb(task.result.longitude, task.result.latitude, resultRange)
                            }
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
                            val olItem = OverlayItem(trophy.trophyid.toString(), null, GeoPoint(trophy.latitude, trophy.longitude))
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
                                        val distanceInMeters = getDistanceToTrophy(task.result.latitude, task.result.longitude, item.point.latitude, item.point.longitude )
                                        if(distanceInMeters < 8050.0){
                                            activityCallBack!!.onTrophyClick(item.title.toLong(), item.point.latitude, item.point.longitude)
                                        } else {
                                            Toast.makeText(context, "Get closer to the target, your distance must be less than 150 m (distance is "+distanceInMeters.toInt()+" meters)", Toast.LENGTH_SHORT).show()
                                        }

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

        val distanceInMeters = myCurrentLocation.distanceTo(clickedMarkerLocation)
        return distanceInMeters
    }

}