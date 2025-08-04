package com.example.arplacement.ui.screens

import android.view.MotionEvent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.arplacement.data.Drill
import com.google.ar.core.*
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARViewScreen(navController: NavController, drill: Drill) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var arSceneView: ARSceneView? by remember { mutableStateOf(null) }
    var currentAnchorNode: AnchorNode? by remember { mutableStateOf(null) }
    var objectsPlaced by remember { mutableIntStateOf(0) }
    var planesDetected by remember { mutableStateOf(false) }
    var isObjectPlaced by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AR View: ${drill.name}") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.5f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            AndroidView(
                factory = { ctx ->
                    ARSceneView(
                        context = ctx,
                        onSessionUpdated = { session, frame ->
                            // ####################################################
                            // ## THIS IS THE FIX TO PREVENT FLICKERING          ##
                            // ####################################################
                            // Check if there are any *tracked* planes in the whole session,
                            // not just planes updated in the current frame.
                            if (!isObjectPlaced) {
                                planesDetected = session.getAllTrackables(Plane::class.java)
                                    .any { it.trackingState == TrackingState.TRACKING }
                            }
                            // ####################################################
                        },
                        sessionConfiguration = { _, config ->
                            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
                        }
                    ).apply {
                        arSceneView = this
                        planeRenderer.isEnabled = true
                        planeRenderer.isVisible = !isObjectPlaced

                        setOnTouchListener { view, motionEvent ->
                            if (motionEvent.action == MotionEvent.ACTION_UP) {
                                view.performClick()

                                if (planesDetected) {
                                    coroutineScope.launch {
                                        val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                                        val validHit = hitResults?.firstOrNull { hitResult ->
                                            val trackable = hitResult.trackable
                                            trackable is Plane && trackable.isPoseInPolygon(hitResult.hitPose)
                                        }

                                        if (validHit != null) {
                                            currentAnchorNode?.let { removeChildNode(it) }

                                            val anchor = validHit.createAnchor()
                                            val anchorNode = AnchorNode(engine, anchor)
                                            val modelLoader = ModelLoader(engine, ctx)
                                            val modelInstance = modelLoader.loadModelInstance(drill.modelPath)

                                            if (modelInstance != null) {
                                                val modelNode = ModelNode(modelInstance, scaleToUnits = 0.1f, centerOrigin = Position(y = -0.05f))
                                                anchorNode.addChildNode(modelNode)
                                                withContext(Dispatchers.Main) {
                                                    addChildNode(anchorNode)
                                                    currentAnchorNode = anchorNode
                                                    objectsPlaced = 1
                                                    isObjectPlaced = true
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            true
                        }
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { view ->
                    view.planeRenderer.isVisible = !isObjectPlaced
                }
            )

            // Animated instruction text
            AnimatedVisibility(
                visible = !isObjectPlaced,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
            ) {
                Text(
                    text = if (planesDetected) "Tap on a detected surface to place the drill" else "Move your phone to detect a surface",
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(50))
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            // Animated clear button
            AnimatedVisibility(
                visible = isObjectPlaced,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(32.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        currentAnchorNode?.let { arSceneView?.removeChildNode(it) }
                        currentAnchorNode = null
                        objectsPlaced = 0
                        isObjectPlaced = false
                        Toast.makeText(context, "Object cleared", Toast.LENGTH_SHORT).show()
                    },
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(Icons.Default.Clear, "Clear object")
                }
            }
        }
    }
}