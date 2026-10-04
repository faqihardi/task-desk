package com.klmpk9.taskdesk.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.klmpk9.taskdesk.ui.screens.create.CreateScreen
import com.klmpk9.taskdesk.ui.screens.home.HomeScreen
import com.klmpk9.taskdesk.ui.screens.detail.DetailScreen

// === Definisi Animasi ===

private val enterForward: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(tween(300)) + slideInHorizontally(tween(300)) { fullWidth -> fullWidth }
}

private val exitForward: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { fullWidth -> -fullWidth }
}

private val enterBack: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    fadeIn(tween(300)) + slideInHorizontally(tween(300)) { fullWidth -> -fullWidth }
}

private val exitBack: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { fullWidth -> fullWidth }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Home,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        // === HOME SCREEN ===
        composable<Home>(
            enterTransition = enterForward,
            exitTransition = exitForward,
            popEnterTransition = enterBack,
            popExitTransition = exitBack
        ) {
            HomeScreen(
                navController = navController
            )
        }

        // === CREATE SCREEN
        composable<Create>(
            enterTransition = enterForward,
            exitTransition = exitForward,
            popEnterTransition = enterBack,
            popExitTransition = exitBack
        ) {
            CreateScreen(navController = navController)
        }

        // === DETAIL SCREEN ===
        composable<Detail>(
            enterTransition = enterForward,
            exitTransition = exitForward,
            popEnterTransition = enterBack,
            popExitTransition = exitBack
        ) { backStackEntry ->
            val detailRoute = backStackEntry.toRoute<Detail>()
            val ticketId = detailRoute.ticketId

            DetailScreen(
                ticketId = ticketId,
                navController = navController
            )
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