package com.tfl.tubelinestatus.test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest

@ExperimentalCoroutinesApi
class ViewModelFlowCollector<S, E>(
    private val stateFlow: Flow<S>,
    private val eventFlow: Flow<E>,
    private val dispatcher: TestCoroutineDispatcher
) {

    fun test(test: suspend (List<S>, List<E>) -> Unit): Unit = dispatcher.runBlockingTest {
        val states = mutableListOf<S>()
        val stateJob = launch { stateFlow.toList(states) }
        val events = mutableListOf<E>()
        val eventsJob = launch { eventFlow.toList(events) }
        test(states, events)
        stateJob.cancel()
        eventsJob.cancel()
    }
}