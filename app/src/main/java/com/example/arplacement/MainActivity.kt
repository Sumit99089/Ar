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
    var planesDetected by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { ctx ->
                io.github.sceneview.ar.ARSceneView(
                    context = ctx,
                    onSessionUpdated = { session, frame ->
                        // Count detected planes
                        val planes = session.getAllTrackables(com.google.ar.core.Plane::class.java)
                        planesDetected = planes.count { it.trackingState == com.google.ar.core.TrackingState.TRACKING }
                    }
                ).apply {
                    // Enable plane detection
                    planeRenderer.isEnabled = true

                    // Simple touch listener for now
                    setOnTouchListener { _, motionEvent ->
                        if (motionEvent.action == android.view.MotionEvent.ACTION_UP) {
                            if (planesDetected > 0) {
                                Toast.makeText(context, "✅ Tapped! $planesDetected planes detected. Placed $drillName marker", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "❌ No planes detected yet. Keep moving device over flat surfaces.", Toast.LENGTH_SHORT).show()
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
                    text = "Planes detected: $planesDetected",
                    color = if (planesDetected > 0) Color.Green else Color.Yellow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (planesDetected > 0)
                        "✅ Tap anywhere to place marker"
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
                containerColor = Color.Blue.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "💡 Tips:\n• Use good lighting\n• Move slowly over tables/floors\n• Look for white dots on surfaces",
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}