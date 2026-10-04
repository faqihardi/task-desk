package com.klmpk9.taskdesk.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
    ) {

        // === HOME SCREEN (Tahap 5) ===
        composable<Home> {
            // Placeholder — akan diganti dengan HomeScreen(navController)
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Home")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navController.navigate(Create) }) {
                    Text("Go to Create")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate(Detail(ticketId = "TEST-001")) }) {
                    Text("Go to Detail")
                }
            }
        }

        // === CREATE SCREEN (Tahap 6) ===
        composable<Create> {
            // Placeholder — akan diganti dengan CreateScreen(navController)
            PlaceholderScreen(name = "Create")
        }

        // === DETAIL SCREEN (Tahap 7) ===
        composable<Detail> { backStackEntry ->
            val detailRoute = backStackEntry.toRoute<Detail>()
            val ticketId = detailRoute.ticketId

            // Placeholder — akan diganti dengan DetailScreen(ticketId, navController)
            PlaceholderScreen(name = "Detail\nID: $ticketId")
        }
    }
}


@Composable
private fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = name)
    }
}