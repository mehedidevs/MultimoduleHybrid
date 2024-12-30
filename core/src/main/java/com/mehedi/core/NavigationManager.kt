// NavigationManager.kt
package com.mehedi.core


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class NavigationManager : ViewModel() {
    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    sealed class NavigationEvent {
        data class NavigateTo(val destination: NavigationDestination) : NavigationEvent()
        data class NavigateToRoute(val route: String) : NavigationEvent()
        object NavigateBack : NavigationEvent()
    }

    fun navigateTo(destination: NavigationDestination) {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateTo(destination))
        }
    }

    fun navigateToRoute(route: String) {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateToRoute(route))
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvent.emit(NavigationEvent.NavigateBack)
        }
    }
}