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
import org.mockito.Mockito.times
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
            NetworkResponse.Success(generateTubelineStatusResponses())
        )
        whenever(mockUIModelMapper.mapNetworkResponseToUIModel(generateTubelineStatusResponses())).thenReturn(
            generateTubelineStatusUIModelsData()
        )

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        Assert.assertEquals(states.last().tubelineStatus, generateTubelineStatusUIModelsData())
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
    fun `GIVEN we have tube status in the state WHEN getTubelineStatuses() is called THEN display data from state and do not call repo`() = collector.test { states, _ ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(
            NetworkResponse.Success(generateTubelineStatusResponses())
        )
        whenever(mockUIModelMapper.mapNetworkResponseToUIModel(generateTubelineStatusResponses())).thenReturn(
            generateTubelineStatusUIModelsData()
        )
        tubelineStatusViewModel.getTubelineStatuses()

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        verify(mockTubelineStatusRepository, times(1)).getAllTubelineStatuses()
    }

    @Test
    fun `GIVEN failed network response with error code -1 WHEN getTubelineStatuses() is called THEN emit ShowError event with network error message`() = collector.test { _, events ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(NetworkResponse.Error(ERROR_CODE_MINUS_ONE))
        whenever(mockUIErrorMapper.mapErrorCodeToMessage(ERROR_CODE_MINUS_ONE)).thenReturn(NETWORK_ERROR_MESSAGE)

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        val expectedEvents = listOf(TubelineStatusEvent.ShowError(NETWORK_ERROR_MESSAGE))
        assertEquals(expectedEvents, events)
    }

    @Test
    fun `GIVEN failed network response with error code -2 WHEN getTubelineStatuses() is called THEN emit ShowError event with unknown error message`() = collector.test { _, events ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(NetworkResponse.Error(ERROR_CODE_MINUS_TWO))
        whenever(mockUIErrorMapper.mapErrorCodeToMessage(ERROR_CODE_MINUS_TWO)).thenReturn(UNKOWN_ERROR_MESSAGE)

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        val expectedEvents = listOf(TubelineStatusEvent.ShowError(UNKOWN_ERROR_MESSAGE))
        assertEquals(expectedEvents, events)
    }

    @Test
    fun `GIVEN failed network response WHEN getTubelineStatuses() is called THEN shouldShowLoadingMessage state is set to false`() = collector.test { states, _ ->
        // given
        whenever(mockTubelineStatusRepository.getAllTubelineStatuses()).thenReturn(NetworkResponse.Error(ERROR_CODE_MINUS_ONE))
        whenever(mockUIErrorMapper.mapErrorCodeToMessage(ERROR_CODE_MINUS_ONE)).thenReturn(NETWORK_ERROR_MESSAGE)

        // when
        tubelineStatusViewModel.getTubelineStatuses()

        // then
        Assert.assertFalse(states.last().shouldShowLoadingMessage)
    }

    private fun generateTubelineStatusResponses() =
        mutableListOf(
            TubelineStatusResponse("bakerloo", "name", emptyList()),
            TubelineStatusResponse("central", "name", emptyList()),
            TubelineStatusResponse("circle", "name", emptyList()),
            TubelineStatusResponse("district", "name", emptyList()),
            TubelineStatusResponse("hammersmith-city", "name", emptyList()),
            TubelineStatusResponse("jubilee", "name", emptyList()),
            TubelineStatusResponse("metropolitan", "name", emptyList()),
            TubelineStatusResponse("northern", "name", emptyList()),
            TubelineStatusResponse("piccadilly", "name", emptyList()),
            TubelineStatusResponse("victoria", "name", emptyList()),
            TubelineStatusResponse("waterloo-city", "name", emptyList())
        )

    private fun generateTubelineStatusUIModelsData() =
        arrayListOf(
            TubelineStatusUIModel(BAKERLOO_COLOR_RESOURCE_ID, "id", "name"),
            TubelineStatusUIModel(CENTRAL_COLOR_RESOURCE_ID, "id", "name"),
            TubelineStatusUIModel(CIRCLE_COLOR_RESOURCE_ID, "id", "name"),
            TubelineStatusUIModel(DISTRICT_COLOR_RESOURCE_ID, "id", "name"),
            TubelineStatusUIModel(HAMMERSMITH_CITY__COLOR_RESOURCE_ID, "id", "name"),
            TubelineStatusUIModel(JUBLIEE_COLOR_RESOURCE_ID, "id", "name"),
            TubelineStatusUIModel(METROPOLITAN_COLOR_RESOURCE_ID, "id", "name"),
            TubelineStatusUIModel(NORTHERN_COLOR_RESOURCE_ID, "id", "name"),
            TubelineStatusUIModel(PICCADILLY_COLOR_RESOURCE_ID, "id", "name"),
            TubelineStatusUIModel(VICTORIA_COLOR_RESOURCE_ID, "id", "name"),
            TubelineStatusUIModel(WATERLOO_CITY__COLOR_RESOURCE_ID, "id", "name")
        )

    companion object {
        //Error codes & message
        const val ERROR_CODE_MINUS_ONE = -1
        const val NETWORK_ERROR_MESSAGE = "Network error"
        const val ERROR_CODE_MINUS_TWO = -2
        const val UNKOWN_ERROR_MESSAGE = "Unknown error"

        //color codes
        const val BAKERLOO_COLOR_RESOURCE_ID = 1432
        const val CENTRAL_COLOR_RESOURCE_ID = 1459
        const val CIRCLE_COLOR_RESOURCE_ID = 1456
        const val DISTRICT_COLOR_RESOURCE_ID = 1656
        const val HAMMERSMITH_CITY__COLOR_RESOURCE_ID = 3456
        const val JUBLIEE_COLOR_RESOURCE_ID = 3456
        const val METROPOLITAN_COLOR_RESOURCE_ID = 3458
        const val NORTHERN_COLOR_RESOURCE_ID = 1114
        const val PICCADILLY_COLOR_RESOURCE_ID = 1353
        const val VICTORIA_COLOR_RESOURCE_ID = 1234
        const val WATERLOO_CITY__COLOR_RESOURCE_ID = 6789
    }

}