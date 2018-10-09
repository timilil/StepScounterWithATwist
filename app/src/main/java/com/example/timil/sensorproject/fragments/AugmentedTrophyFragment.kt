package com.example.timil.sensorproject.fragments

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.timil.sensorproject.database.TrophyDB
import com.example.timil.sensorproject.entities.Trophy
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import org.jetbrains.anko.doAsync
import java.util.concurrent.CompletableFuture

class AugmentedTrophyFragment: ArFragment() {

    private var renderable: ModelRenderable? = null
    private var renderableFuture: CompletableFuture<ModelRenderable>? = null
    private val trophyUri = Uri.parse("trophyobjectfile.sfb")
    private var screenX: Int? = null
    private var screenY: Int? = null
    private var activityCallBack: AugmentedFragmentTrophyClickListener? = null
    private var bundle: Bundle? = null

    interface AugmentedFragmentTrophyClickListener {
        fun onARTrophyClick()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // instantiate the 3D trophy model click listener
        activityCallBack = context as AugmentedFragmentTrophyClickListener

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View? {
        // get screen center data passed in bundle
        bundle = this.arguments
        if(bundle != null){
            screenX = bundle?.getInt("x")
            screenY = bundle?.getInt("y")
        }

        val view = super.onCreateView(inflater, container, savedInstanceState)

        this.arSceneView.scene.addOnUpdateListener{ _ ->
            this.planeDiscoveryController.hide()
            add3dObject()
        }
        renderableFuture = ModelRenderable.builder()
                .setSource(context, trophyUri)
                .build()
        renderableFuture!!.thenAccept{ it -> renderable = it }

        return view
    }

    private fun add3dObject(){
        val frame = this.arSceneView.arFrame
        val hits: List<HitResult>
        if (frame != null && renderable != null){
            hits = frame.hitTest(screenX!!.toFloat(), screenY!!.toFloat())
            for (hit in hits){
                val trackable = hit.trackable
                // create the anchor and nodes if the trackable area is an AR plane
                if (trackable is Plane){
                    val anchor = hit!!.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(this.arSceneView.scene)
                    val mNode = TransformableNode(this.transformationSystem)
                    mNode.setParent(anchorNode)
                    mNode.renderable = renderable
                    mNode.select()
                    renderable = null
                    mNode.setOnTapListener { _, _ ->
                        // remove the node, delete trophy location from DB and navigate back to previous view(which in this case is map fragment)
                        anchorNode.removeChild(mNode)
                        val db = TrophyDB.get(context!!)
                        doAsync {
                            db.trophyDao().delete(Trophy(bundle!!.getLong("id"), bundle!!.getDouble("latitude"), bundle!!.getDouble("longitude")))
                        }
                        activityCallBack!!.onARTrophyClick()
                    }
                    break
                }
            }
        }
    }

}


