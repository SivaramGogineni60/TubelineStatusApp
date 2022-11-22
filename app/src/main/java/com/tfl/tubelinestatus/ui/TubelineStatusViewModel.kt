package com.tfl.tubelinestatus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfl.tubelinestatus.network.NetworkResponse
import com.tfl.tubelinestatus.repositories.api.TubelineStatusRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TubelineStatusViewModel(
    private val repository: TubelineStatusRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(TubelineStatusViewState())
    private val _events = MutableSharedFlow<TubelineStatusEvent>()

    val viewState: Flow<TubelineStatusViewState> = _state
    val events: Flow<TubelineStatusEvent> = _events

    fun getTubelineStatuses(scope: CoroutineScope = viewModelScope) {
        showLoadingState()
        scope.launch {
            callTubelineStatuses()
        }
    }

    private suspend fun callTubelineStatuses(scope: CoroutineScope = viewModelScope) {
        scope.launch {
            withContext(dispatcher) {
                repository.getAllTubelineStatuses()
            }.apply {
                when (this) {
                    is NetworkResponse.Success -> {
                        _events.emit(TubelineStatusEvent.ShowTubelineStatuses(this.data))
                        hideLoadingState()
                    }
                    is NetworkResponse.Error -> {
                        _events.emit(TubelineStatusEvent.ShowError(this.code))
                        hideLoadingState()
                    }
                }
            }
        }
    }

    private fun showLoadingState() {
        _state.value = _state.value.copy(shouldShowLoadingMessage = true)
    }

    private fun hideLoadingState() {
        _state.value = _state.value.copy(shouldShowLoadingMessage = false)
    }
}