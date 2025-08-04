package com.example.arplacement.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Drill(
    val name: String,
    val description: String,
    val icon: ImageVector,
    val tips: String,
    val color: Color,
    val modelPath: String
)