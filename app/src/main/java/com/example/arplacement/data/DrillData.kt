package com.example.arplacement.data


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.ui.graphics.Color

val drills = listOf(
    Drill(
        "Heavy Duty Drill",
        "A powerful cordless drill for heavy-duty construction tasks and masonry work.",
        Icons.Default.Build,
        "Use with care, ensure battery is charged. Wear safety goggles.",
        Color(0xFF2196F3),
        "models/cube.glb"
    ),
    Drill(
        "Precision Drill",
        "A compact drill perfect for precision work and detailed craftsmanship.",
        Icons.Default.Build,
        "Ideal for small spaces, check bit alignment. Use low speed for delicate materials.",
        Color(0xFF4CAF50),
        "models/cone.glb"
    ),
    Drill(
        "All-Purpose Drill",
        "A versatile drill suitable for general household and workshop use.",
        Icons.Default.Build,
        "Adjust speed for different materials. Great for beginners.",
        Color(0xFFFF9800),
        "models/sphere.glb"
    )
)