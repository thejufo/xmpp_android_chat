package com.thejufo.chat.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thejufo.chat.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
  val isLoggedIn: Boolean = false,
  val isLoading: Boolean = false,
  val message: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
  private val authRepository: AuthRepository
) : ViewModel() {

  val authState = MutableStateFlow(AuthState(isLoading = true))

  init {
    viewModelScope.launch(Dispatchers.IO) {
      val isLoggedIn = authRepository.isLoggedIn()
      authState.value = AuthState(isLoggedIn = isLoggedIn, isLoading = isLoggedIn)
    }
  }

  fun login(credentials: Pair<String, String>) {
    viewModelScope.launch(Dispatchers.IO) {
      authState.value = AuthState(isLoading = true)
      try {
        authRepository.login(credentials)
        authState.value = AuthState(isLoggedIn = true, isLoading = true, message = "Logged in successfully")
      } catch (e: Exception) {
        authState.value = AuthState(isLoggedIn = false, isLoading = false, message = e.message)
      }
    }
  }
}
