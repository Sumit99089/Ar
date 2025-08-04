package com.example.arplacement.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.arplacement.data.Drill

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrillDetailScreen(navController: NavController, drill: Drill) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(drill.name) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("ar_view/${drill.name}") },
                containerColor = drill.color,
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Start AR") },
                text = { Text("Start AR Drill", fontWeight = FontWeight.Bold) }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(drill.color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = drill.icon,
                        contentDescription = drill.name,
                        modifier = Modifier.size(120.dp),
                        tint = drill.color
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.titleLarge,
                    color = drill.color
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = drill.description,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 24.dp))
            }

            item {
                Text(
                    text = "Safety Tips",
                    style = MaterialTheme.typography.titleLarge,
                    color = drill.color
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = drill.tips,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 24.sp
                )
                Spacer(modifier = Modifier.height(100.dp)) // Spacer for FAB
            }
        }
    }
}