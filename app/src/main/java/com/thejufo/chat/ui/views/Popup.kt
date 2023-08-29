package com.thejufo.chat.ui.views

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.*

@Composable
fun PopupMenu(
  modifier: Modifier = Modifier,
  onLogoutClick: () -> Unit = {},
) {
  val expandedState = remember { mutableStateOf(false) }

  Column(
    modifier = Modifier.wrapContentSize(Alignment.TopStart)
  ) {
    IconButton(onClick = { expandedState.value = true }) {
      Icon(Icons.Default.MoreVert, contentDescription = null)
    }

    DropdownMenu(
      expanded = expandedState.value,
      onDismissRequest = { expandedState.value = false }
    ) {
      DropdownMenuItem(onClick = {
        onLogoutClick()
        expandedState.value = false
      }) {
        Text(text = "Logout")
      }
    }
  }
}