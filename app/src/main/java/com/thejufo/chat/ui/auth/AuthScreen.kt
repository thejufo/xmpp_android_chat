package com.thejufo.chat.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.thejufo.chat.R
import com.thejufo.chat.ui.theme.white
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
  onAuthSuccess: () -> Unit = {},
  viewModel: AuthViewModel = hiltViewModel(),
) {

  val scaffoldState = rememberScaffoldState()
  val authState by viewModel.authState.collectAsStateWithLifecycle()

  val onLogin = { credentials: Pair<String, String> ->
    viewModel.login(credentials)
  }

  LaunchedEffect(authState) {
    if (authState.message != null) {
      scaffoldState.snackbarHostState.showSnackbar(
        message = authState.message!!,
        duration = SnackbarDuration.Short
      )
    }

    if (authState.isLoggedIn) {
      onAuthSuccess()
    }
  }

  Scaffold(
    scaffoldState = scaffoldState,
  ) {
    Box(modifier = Modifier.padding(it)) {
      AuthScreen(Modifier.padding(it), onLogin)
      if (authState.isLoading) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        ) {
          CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            strokeWidth = 2.dp
          )
        }
      }
    }
  }
}

@Composable
private fun AuthScreen(
  modifier: Modifier = Modifier,
  onLogin: (Pair<String, String>) -> Unit = {}
) {

  var username by remember { mutableStateOf("901092505722") }
  var password by remember { mutableStateOf("uriNrCiy5ZO/B4ulFVY1zPejWWQ0OppKP9yfaMK9Hkr7/0w5byjn0ezM7cl4tjNuNJyMarXkQwvg1a+jeb7j+g==") }
  var keepLoggedIn by remember { mutableStateOf(true) }

  val scrollState = rememberScrollState()

  Column(
    modifier = modifier
      .fillMaxHeight()
      .padding(32.dp)
      .verticalScroll(enabled = true, state = scrollState),
    verticalArrangement = Arrangement.Center
  ) {

    Text("Sign In", fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
    Spacer(modifier = Modifier.padding(3.dp))
    Text(
      "Stay Connected, Anytime, Anywhere\nSign in to Chat",
      color = Color.Gray,
      fontSize = 14.sp,
      fontWeight = FontWeight.Normal
    )

    Spacer(modifier = Modifier.padding(16.dp))
    OutlinedTextField(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.Transparent),
      shape = RoundedCornerShape(12.dp),
      value = username,
      onValueChange = { username = it },
      singleLine = true,
      label = { Text("Username") },
      keyboardActions = KeyboardActions {
        this.defaultKeyboardAction(ImeAction.Next)
      },
      leadingIcon = {
        Icon(
          painter = painterResource(id = R.drawable.ic_user),
          contentDescription = "",
          Modifier.size(18.dp)
        )
      }
    )

    Spacer(modifier = Modifier.padding(8.dp))
    OutlinedTextField(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.Transparent),
      shape = RoundedCornerShape(12.dp),
      value = password,
      onValueChange = { password = it },
      label = { Text("Password") },
      singleLine = true,
      leadingIcon = {
        Icon(
          painter = painterResource(id = R.drawable.ic_key),
          contentDescription = "",
          Modifier.size(18.dp)
        )
      }
    )

    Row(
      modifier = Modifier
        .align(Alignment.Start)
        .padding(top = 16.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Checkbox(
        modifier = Modifier.size(24.dp),
        checked = keepLoggedIn, onCheckedChange = { keepLoggedIn = it },
      )
      Text(
        "Keep me logged in",
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = Color.Gray
      )
    }

    Button(
      modifier = Modifier
        .padding(top = 24.dp)
        .fillMaxWidth()
        .height(48.dp),
      elevation = ButtonDefaults.elevation(0.dp),
      shape = RoundedCornerShape(10.dp),
      onClick = { onLogin(Pair(username, password)) }) {
      Text("Sign In", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
    }

    Row(
      modifier = Modifier.padding(top = 24.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Divider(modifier = Modifier.weight(1.0F))
      Text("Or", modifier = Modifier.padding(horizontal = 24.dp), color = Color.Gray)
      Divider(modifier = Modifier.weight(1.0F))
    }

    TextButton(
      modifier = Modifier
        .padding(top = 8.dp)
        .fillMaxWidth(),
      enabled = false,
      onClick = { /*TODO*/ }) {
      Text(text = "Create new account")
    }
  }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun AuthScreenPreview() {
  AuthScreen {

  }
}