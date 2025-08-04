package com.example.arplacement.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.arplacement.data.drills
import com.example.arplacement.ui.screens.ARViewScreen
import com.example.arplacement.ui.screens.DrillDetailScreen
import com.example.arplacement.ui.screens.DrillSelectionScreen

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
            val drill = drills.find { it.name == drillName }
            if (drill != null) {
                ARViewScreen(navController = navController, drill = drill)
            }
        }
    }
}