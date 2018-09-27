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

}
