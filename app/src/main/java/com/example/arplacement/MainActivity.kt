package com.example.arplacement

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.ar.node.PlacementMode
import com.example.arplacement.ui.theme.ARPlacementTheme

// Data class for Drill
data class Drill(
    val name: String,
    val description: String,
    val imageResId: Int,
    val tips: String
)

// Mock drill data
val drills = listOf(
    Drill("Drill 1", "A powerful cordless drill for heavy-duty tasks.", R.drawable.drill1, "Use with care, ensure battery is charged."),
    Drill("Drill 2", "A compact drill for precision work.", R.drawable.drill2, "Ideal for small spaces, check bit alignment."),
    Drill("Drill 3", "A versatile drill for general use.", R.drawable.drill3, "Adjust speed for different materials.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ARPlacementTheme {
                AppNavigation()
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

@Composable
fun DrillSelectionScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select a Drill",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(drills) { drill ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("drill_detail/${drill.name}") },
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = drill.imageResId),
                            contentDescription = drill.name,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = drill.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DrillDetailScreen(navController: NavController, drill: Drill) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = drill.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Image(
            painter = painterResource(id = drill.imageResId),
            contentDescription = drill.name,
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
        )
        Text(
            text = "Description",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = drill.description,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = "Tips",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = drill.tips,
            fontSize = 16.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Button(
            onClick = { navController.navigate("ar_view/${drill.name}") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Start AR Drill")
        }
    }
}

@Composable
fun ARViewScreen(drillName: String) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = {
                ArSceneView(context).apply {
                    planeFindingMode = ArSceneView.PlaneFindingMode.HORIZONTAL
                    instructionsEnabled = true
                    instructionsText = "Tap on ground to place drill marker"
                }
            },
            modifier = Modifier.fillMaxSize()
        ) { arSceneView ->
            // Create a model node for the 3D object
            val modelNode = ArModelNode(placementMode = PlacementMode.PLANE_HORIZONTAL).apply {
                loadModelGlbAsync(
                    glbFileLocation = "models/cube.glb",
                    onLoaded = { _ ->
                        Toast.makeText(context, "Placed $drillName marker", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            arSceneView.addChild(modelNode)
            arSceneView.onTapAr = { hitResult, _ ->
                modelNode.anchor = hitResult.createAnchor()
            }
        }
    }
}
