package com.example.timil.sensorproject.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.HitTestResult
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.BaseArFragment
import com.google.ar.sceneform.ux.TransformableNode

class AugmentedTrophyFragment: ArFragment() {

    private var trophyRenderable: ModelRenderable? = null
    private var name: String? = null
    private val DOUBLE_CLICK_TIME_DELTA: Long = 300// milliseconds

    var lastClickTime: Long = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState:
    Bundle?): View? {

        val view = super.onCreateView(inflater, container, savedInstanceState)

        return view
    }

    /*override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            Log.d("DBG", (this as Any).javaClass.simpleName + " is NOT on screen")
        } else {
            Log.d("DBG", (this as Any).javaClass.simpleName + " is on screen")
            if (name != null) {
                Toast.makeText(context, "Hint: Wait for the dots to appear on screen to add furniture. Double tap to delete furniture.", Toast.LENGTH_LONG).show()

                val modelUri = Uri.parse(name)
                val renderableFuture = ModelRenderable.builder()
                        .setSource(context, modelUri)
                        .build()
                renderableFuture.thenAccept { it -> trophyRenderable = it }

                this.setOnTapArPlaneListener(
                        object : BaseArFragment.OnTapArPlaneListener {
                            @SuppressLint("ClickableViewAccessibility")
                            override fun onTapPlane(hitResult: HitResult?, plane: Plane?, motionEvent: MotionEvent?) {
                                if (trophyRenderable == null) {
                                    return@onTapPlane
                                }

                                val anchor = hitResult!!.createAnchor()
                                val anchorNode = AnchorNode(anchor)
                                anchorNode.setParent(arSceneView.scene)
                                val viewNode = TransformableNode(transformationSystem)
                                viewNode.setParent(anchorNode)
                                viewNode.setOnTapListener(object : Node.OnTapListener {
                                    override fun onTap(hitTestResult: HitTestResult, motionEvent: MotionEvent) {
                                        //Log.d("DBG", "Touch")

                                        val clickTime = System.currentTimeMillis()
                                        if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                                            //Log.d("DBG", "double tap")
                                            anchorNode.removeChild(viewNode)
                                            Toast.makeText(context, "Deleted furniture", Toast.LENGTH_SHORT).show()
                                            lastClickTime = 0
                                        }
                                        lastClickTime = clickTime

                                    }
                                })
                                viewNode.renderable = trophyRenderable
                                viewNode.select()
                            }
                        }
                )
            }
        }
    }*/


    //TODO USE THESE VARIABLES FOR AR
    /*
    var testRenderable: ModelRenderable? = null
    var otherRenderable: ModelRenderable? = null
    var fragment: ArFragment? = null
    var renderableFuture: CompletableFuture<ModelRenderable>? = null
    val bugUri = Uri.parse("bug.sfb")
    val splatUri = Uri.parse("splat.sfb")
     */
    
    //TODO THIS GOES IN ONCREATE...
    /*
    fragment!!.arSceneView.scene.addOnUpdateListener { _ ->
            //fragment!!.planeDiscoveryController.hide()
            addObject()
        }
     */

    //TODO THESE ALSO IN ONCREATE
    /*
    //fragment!!.planeDiscoveryController.hide()

        renderableFuture = ModelRenderable.builder()
                .setSource(fragment!!.context, bugUri)
                .build()
        renderableFuture!!.thenAccept{ it -> testRenderable = it }

        renderableFuture = ModelRenderable.builder()
                .setSource(fragment!!.context, splatUri)
                .build()
        renderableFuture!!.thenAccept{ it -> otherRenderable = it }
     */

    //TODO MODIFY THIS FUNCTIONS TO SERVE OUR NEEDS
    /*
    private fun addObject(){
        val frame = fragment!!.arSceneView.arFrame
        val pt = getScreenCenter()
        val hits: List<HitResult>
        if (frame != null && testRenderable != null){
            hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits){
                val trackable = hit.trackable
                if (trackable is Plane){
                    val anchor = hit!!.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(fragment!!.arSceneView.scene)
                    val mNode = TransformableNode(fragment!!.transformationSystem)
                    mNode.setParent(anchorNode)
                    mNode.renderable = testRenderable
                    mNode.select()
                    val bug = testRenderable
                    testRenderable = null
                    mNode.setOnTapListener { _, _ ->
                        if (mNode.renderable == bug) {
                            mNode.renderable = otherRenderable
                            mNode.select()
                            points.text = (points.text.toString().toInt() + 1).toString()
                            Toast.makeText(this, "Hit! +1p", Toast.LENGTH_SHORT).show()
                            renderableFuture = ModelRenderable.builder()
                                    .setSource(fragment!!.context, bugUri)
                                    .build()
                            renderableFuture!!.thenAccept{ it -> testRenderable = it }
                        }
                        else {
                            anchorNode.removeChild(mNode)
                            testRenderable = null
                            Toast.makeText(this, "cleaned up", Toast.LENGTH_SHORT).show()

                        }
                    }
                    break
                }
            }
        }
    }
     */

    //TODO USE THIS FUNCTION FOR AR
    /*
    private fun getScreenCenter(): android.graphics.PointF{
        val vw = findViewById<View>(android.R.id.content)
        return android.graphics.PointF(vw.width/(Math.random()*3).toFloat(), vw.height/(Math.random()*3).toFloat())
    }
     */

}


