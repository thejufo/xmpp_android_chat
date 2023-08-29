package com.thejufo.chat

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.thejufo.chat.data.models.Message
import com.thejufo.chat.ui.Graph

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

fun NavGraphBuilder.animatedComposable(
  destination: Graph,
  content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
  composable(
    route = destination.route,
    arguments = destination.arguments,
    enterTransition = {
      slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(500)
      )
    },
    exitTransition = {
      slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Left,
        animationSpec = tween(500)
      )
    },
    popEnterTransition = {
      slideIntoContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(500)
      )
    },
    popExitTransition = {
      slideOutOfContainer(
        AnimatedContentTransitionScope.SlideDirection.Right,
        animationSpec = tween(500)
      )
    },
  ) {
    content(it)
  }
}

fun Message.icon(): Int {
  return when(status) {
    Message.Status.SENDING -> R.drawable.ic_sending
    Message.Status.SENT -> R.drawable.ic_sent
    Message.Status.RECEIVED -> R.drawable.ic_sent
    else -> R.drawable.ic_failed
  }
}