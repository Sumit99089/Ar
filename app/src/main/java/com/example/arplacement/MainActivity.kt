package com.example.arplacement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.arplacement.ui.theme.ARPlacementTheme
import io.github.sceneview.ar.ARSceneView
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.math.Position
import io.github.sceneview.loaders.ModelLoader
import com.google.ar.core.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

// Data class for Drill
data class Drill(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val tips: String,
    val color: Color
)

// Mock drill data with icons instead of missing image resources
val drills = listOf(
    Drill(
        "Heavy Duty Drill",
        "A powerful cordless drill for heavy-duty construction tasks and masonry work.",
        Icons.Default.Build,
        "Use with care, ensure battery is charged. Wear safety goggles.",
        Color(0xFF2196F3)
    ),
    Drill(
        "Precision Drill",
        "A compact drill perfect for precision work and detailed craftsmanship.",
        Icons.Default.Build,
        "Ideal for small spaces, check bit alignment. Use low speed for delicate materials.",
        Color(0xFF4CAF50)
    ),
    Drill(
        "All-Purpose Drill",
        "A versatile drill suitable for general household and workshop use.",
        Icons.Default.Build,
        "Adjust speed for different materials. Great for beginners.",
        Color(0xFFFF9800)
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ARPlacementTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "drill_selection") {
        composable("drill_selection") {
            DrillSelectionScreen(navController)
        }
        composable("drill_detail/{drillName}") { backStackEntry ->
            val drillName = backStackEntry.arguments?.getString("drillName") ?: ""
            val drill = drills.find { it.name == drillName }
            drill?.let {
                DrillDetailScreen(navController, it)
            }
        }
        composable("ar_view/{drillName}") { backStackEntry ->
            val drillName = backStackEntry.arguments?.getString("drillName") ?: ""
            ARViewScreen(drillName)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrillSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "AR Drill Placement",
                    fontWeight = FontWeight.Bold
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Select a Drill",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(drills) { drill ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("drill_detail/${drill.name}") },
                    elevation = CardDefaults.cardElevation(6.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    drill.color.copy(alpha = 0.2f),
                                    RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = drill.icon,
                                contentDescription = drill.name,
                                modifier = Modifier.size(32.dp),
                                tint = drill.color
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = drill.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Tap to view details",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrillDetailScreen(navController: NavController, drill: Drill) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TopAppBar(
            title = { Text(drill.name) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Drill Icon Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    drill.color.copy(alpha = 0.1f),
                    RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = drill.icon,
                contentDescription = drill.name,
                modifier = Modifier.size(100.dp),
                tint = drill.color
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Description Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Description",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = drill.color,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = drill.description,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tips Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Safety Tips",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = drill.color,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = drill.tips,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Start AR Button
        Button(
            onClick = { navController.navigate("ar_view/${drill.name}") },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = drill.color)
        ) {
            Text(
                text = "Start AR Drill",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ARViewScreen(drillName: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var planesDetected by remember { mutableStateOf(0) }
    var objectsPlaced by remember { mutableStateOf(0) }
    var arSceneView: ARSceneView? by remember { mutableStateOf(null) }
    var currentAnchorNode: AnchorNode? by remember { mutableStateOf(null) }

    // Get drill color for the cube
    val drill = drills.find { it.name == drillName }
    val drillColor = drill?.color ?: Color.Blue

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                ARSceneView(
                    context = ctx,
                    onSessionUpdated = { session, frame ->
                        // Count detected planes
                        val planes = session.getAllTrackables(Plane::class.java)
                        planesDetected = planes.count { it.trackingState == TrackingState.TRACKING }
                    }
                ).apply {
                    arSceneView = this

                    // Enable plane detection
                    planeRenderer.isEnabled = true
                    planeRenderer.isVisible = true

                    // Handle touch events for placing objects
                    setOnTouchListener { view, motionEvent ->
                        if (motionEvent.action == android.view.MotionEvent.ACTION_UP) {
                            if (planesDetected > 0) {
                                coroutineScope.launch {
                                    try {
                                        // Perform hit test to find where user tapped on a plane
                                        val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)

                                        val validHit = hitResults?.firstOrNull { hitResult ->
                                            val trackable = hitResult.trackable
                                            trackable is Plane && trackable.isPoseInPolygon(hitResult.hitPose)
                                        }

                                        if (validHit != null) {
                                            // Remove previous object (only allow one at a time)
                                            currentAnchorNode?.let { oldNode ->
                                                removeChildNode(oldNode)
                                            }

                                            // Create anchor at hit location
                                            val anchor = validHit.createAnchor()

                                            // Create AnchorNode with your cube.glb model
                                            val anchorNode = AnchorNode(
                                                engine = engine,
                                                anchor = anchor
                                            )

                                            // Load the model using ModelLoader
                                            coroutineScope.launch {
                                                try {
                                                    // Create a ModelLoader instance
                                                    val modelLoader = ModelLoader(
                                                        engine = engine,
                                                        context = ctx
                                                    )

                                                    // Load the model instance from assets
                                                    val modelInstance = modelLoader.loadModelInstance("models/cube.glb")

                                                    if (modelInstance != null) {
                                                        val modelNode = ModelNode(
                                                            modelInstance = modelInstance,
                                                            scaleToUnits = 0.5f, // Increase scale to 50cm for visibility
                                                            centerOrigin = Position(0.0f, 0.25f, 0.0f) // Lift it higher
                                                        )

                                                        // Add some debug info
                                                        println("DEBUG: ModelNode created with scale 0.5f")
                                                        println("DEBUG: ModelInstance entities: ${modelInstance.entities.size}")

                                                        anchorNode.addChildNode(modelNode)

                                                        // Update UI on main thread
                                                        withContext(Dispatchers.Main) {
                                                            addChildNode(anchorNode)
                                                            currentAnchorNode = anchorNode
                                                            objectsPlaced++

                                                            Toast.makeText(
                                                                context,
                                                                "✅ $drillName cube placed! Size: 50cm, Entities: ${modelInstance.entities.size}",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    } else {
                                                        withContext(Dispatchers.Main) {
                                                            Toast.makeText(
                                                                context,
                                                                "❌ Failed to load cube model - modelInstance is null",
                                                                Toast.LENGTH_LONG
                                                            ).show()
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    withContext(Dispatchers.Main) {
                                                        Toast.makeText(
                                                            context,
                                                            "❌ Error loading model: ${e.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                    e.printStackTrace()
                                                }
                                            }
                                            objectsPlaced++

                                            Toast.makeText(
                                                context,
                                                "✅ $drillName marker placed! ($objectsPlaced placed)",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        } else {
                                            Toast.makeText(
                                                context,
                                                "❌ No plane detected at tap location",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "❌ Failed to place object: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "❌ No planes detected yet. Keep moving device over flat surfaces.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        true
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Status overlay
        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = drillName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Planes: $planesDetected | Objects: $objectsPlaced",
                    color = if (planesDetected > 0) Color.Green else Color.Yellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (planesDetected > 0)
                        "✅ Tap on white dots to place drill marker"
                    else
                        "Move device slowly over flat surfaces",
                    color = Color.White,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Instructions overlay
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = drillColor.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "💡 Tips:\n• Use good lighting\n• Move slowly over tables/floors\n• Tap on white plane dots to place marker\n• Only one marker at a time",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp)
            )
        }

        // Clear button
        if (objectsPlaced > 0) {
            Button(
                onClick = {
                    currentAnchorNode?.let { node ->
                        arSceneView?.removeChildNode(node)
                        currentAnchorNode = null
                    }
                    objectsPlaced = 0
                    Toast.makeText(context, "🗑️ All objects cleared", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.8f)
                )
            ) {
                Text("Clear", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}