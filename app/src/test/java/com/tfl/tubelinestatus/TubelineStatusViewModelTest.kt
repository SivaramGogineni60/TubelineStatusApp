package com.tfl.tubelinestatus

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tfl.tubelinestatus.models.TubelineStatusResponse
import com.tfl.tubelinestatus.network.NetworkResponse
import com.tfl.tubelinestatus.repositories.api.TubelineStatusRepository
import com.tfl.tubelinestatus.test.TestCoroutineRule
import com.tfl.tubelinestatus.ui.TubelineStatusEvent
import com.tfl.tubelinestatus.ui.TubelineStatusViewModel
import com.tfl.tubelinestatus.ui.TubelineStatusViewState
import com.tfl.tubelinestatus.test.ViewModelFlowCollector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class TubelineStatusViewModelTest {
    @get:Rule
    val rule = TestCoroutineRule()

    @Mock
    private lateinit var mockTubelineStatusRepository: TubelineStatusRepository

    private val testDispatcher = Dispatchers.Unconfined
    private lateinit var collector: ViewModelFlowCollector<TubelineStatusViewState, TubelineStatusEvent>
    private lateinit var tubelineStatusViewModel: TubelineStatusViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        tubelineStatusViewModel = TubelineStatusViewModel(mockTubelineStatusRepository, testDispatcher)
        collector = ViewModelFlowCollector(tubelineStatusViewModel.viewState, tubelineStatusViewModel.events, TestCoroutineDispatcher())
    }

    @Test
    fun `WHEN getTubelineStatuses from viewmodel is called THEN getAllTubelineStatuses() from repository should be called`() {
        runBlocking {
            // when
            tubelineStatusViewModel.getTubelineStatuses()

            // then
            verify(mockTubelineStatusRepository).getAllTubelineStatuses()

        }
    }

    @Test
    fun `WHEN getTubelineStatuses from viewmodel is called THEN shouldShowLoadingMessage state is set to true`() = collector.test { states, _ ->
        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        Assert.assertTrue(states[states.size - 1].shouldShowLoadingMessage)
    }

    @Test
    fun `GIVEN successful network response WHEN getTubelineStatuses() is called THEN emit ShowTubelineStatuses event`() = collector.test { _, events ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(
            NetworkResponse.Success(mutableListOf())
        )

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        val expectedEvents = listOf(TubelineStatusEvent.ShowTubelineStatuses(mutableListOf()))
        assertEquals(expectedEvents, events)
    }

    @Test
    fun `GIVEN successful network response WHEN getTubelineStatuses() is called THEN shouldShowLoadingMessage state is set to false`() = collector.test { states, _ ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(
            NetworkResponse.Success(mutableListOf())
        )

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        Assert.assertFalse(states[states.size - 1].shouldShowLoadingMessage)
    }

    @Test
    fun `GIVEN failed network response WHEN getTubelineStatuses() is called THEN emit ShowError event`() = collector.test { _, events ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(
            NetworkResponse.Error(ERROR_CODE)
        )

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        val expectedEvents = listOf(TubelineStatusEvent.ShowError(ERROR_CODE))
        assertEquals(expectedEvents, events)
    }

    @Test
    fun `GIVEN failed network response WHEN getTubelineStatuses() is called THEN shouldShowLoadingMessage state is set to false`() = collector.test { states, _ ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(
            NetworkResponse.Error(ERROR_CODE)
        )

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        Assert.assertFalse(states[states.size - 1].shouldShowLoadingMessage)
    }

    companion object {
        const val ERROR_CODE = 2
    }

}