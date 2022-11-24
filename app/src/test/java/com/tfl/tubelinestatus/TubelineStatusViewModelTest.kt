package com.tfl.tubelinestatus

import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.tfl.tubelinestatus.models.TubelineStatusResponse
import com.tfl.tubelinestatus.network.NetworkResponse
import com.tfl.tubelinestatus.repositories.api.TubelineStatusRepository
import com.tfl.tubelinestatus.test.TestCoroutineRule
import com.tfl.tubelinestatus.test.ViewModelFlowCollector
import com.tfl.tubelinestatus.ui.*
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
    @Mock
    private lateinit var mockUIModelMapper: UIModelMapper
    @Mock
    private lateinit var mockUIErrorMapper: UIErrorMapper

    private val testDispatcher = Dispatchers.Unconfined
    private lateinit var collector: ViewModelFlowCollector<TubelineStatusViewState, TubelineStatusEvent>
    private lateinit var tubelineStatusViewModel: TubelineStatusViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        tubelineStatusViewModel = TubelineStatusViewModel(mockTubelineStatusRepository, testDispatcher, mockUIModelMapper, mockUIErrorMapper)
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
        Assert.assertTrue(states.last().shouldShowLoadingMessage)
    }

    @Test
    fun `GIVEN successful network response WHEN getTubelineStatuses() is called THEN emit ShowTubelineStatuses event`() = collector.test { states, _ ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(
            NetworkResponse.Success(mutableListOf(TubelineStatusResponse("bakerloo", "name", emptyList())))
        )
        whenever(mockUIModelMapper.mapNetworkResponseToUIModel(mutableListOf(TubelineStatusResponse("bakerloo", "name", emptyList())))).thenReturn(
            arrayListOf(TubelineStatusUIModel(BAKERLOO_COLOR_RESOURCE_ID, "id", "name"))
        )

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        Assert.assertEquals(states.last().tubelineStatus, arrayListOf(TubelineStatusUIModel(
            BAKERLOO_COLOR_RESOURCE_ID, "id", "name")))
    }

    @Test
    fun `GIVEN successful network response WHEN getTubelineStatuses() is called THEN shouldShowLoadingMessage state is set to false`() = collector.test { states, _ ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(NetworkResponse.Success(mutableListOf()))

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        Assert.assertFalse(states.last().shouldShowLoadingMessage)
    }

    @Test
    fun `GIVEN failed network response WHEN getTubelineStatuses() is called THEN emit ShowError event`() = collector.test { _, events ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(NetworkResponse.Error(ERROR_CODE))
        whenever(mockUIErrorMapper.mapErrorCodeToMessage(ERROR_CODE)).thenReturn(ERROR_MESSAGE)

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        val expectedEvents = listOf(TubelineStatusEvent.ShowError(ERROR_MESSAGE))
        assertEquals(expectedEvents, events)
    }

    @Test
    fun `GIVEN failed network response WHEN getTubelineStatuses() is called THEN shouldShowLoadingMessage state is set to false`() = collector.test { states, _ ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(NetworkResponse.Error(ERROR_CODE))
        whenever(mockUIErrorMapper.mapErrorCodeToMessage(ERROR_CODE)).thenReturn(ERROR_MESSAGE)

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        Assert.assertFalse(states.last().shouldShowLoadingMessage)
    }

    companion object {
        const val ERROR_CODE = -1
        const val ERROR_MESSAGE = "Network error"
        const val BAKERLOO_COLOR_RESOURCE_ID = 1432
    }

}