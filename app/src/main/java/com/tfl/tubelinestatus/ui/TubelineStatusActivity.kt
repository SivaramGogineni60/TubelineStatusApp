package com.tfl.tubelinestatus.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tfl.tubelinestatus.R
import com.tfl.tubelinestatus.managers.SessionManager
import com.tfl.tubelinestatus.models.TubelineStatusResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel


class TubelineStatusActivity : AppCompatActivity() {
    private val viewModel by viewModel<TubelineStatusViewModel>()
    lateinit var progress: ProgressBar
    lateinit var recyclerView: RecyclerView

    private var stateJob: Job? = null
    private var eventJob: Job? = null

    private val tubelineStatusList = ArrayList<TubelineStatusUiModel>()
    private lateinit var tubelineStatusAdapter: TubelineStatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tubeline_status)
        progress = findViewById(R.id.progress)

        SessionManager.userToken = "copy-your-token-here"
        setupRecyclerView()
        viewModel.getTubelineStatuses()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        tubelineStatusAdapter = TubelineStatusAdapter(tubelineStatusList)
        recyclerView.adapter = tubelineStatusAdapter
    }

    private fun observeViewState() {
        stateJob = lifecycleScope.launch() {
            viewModel.viewState.collect { viewState ->
                showLoadingStatus(viewState.shouldShowLoadingMessage)
            }
        }
    }

    private fun showLoadingStatus(showLoading: Boolean) {
        progress.isVisible = showLoading
    }

    private fun observeEvents() {
        eventJob = lifecycleScope.launch {
            viewModel.events.collect { event ->
                when (event) {
                    is TubelineStatusEvent.ShowTubelineStatuses -> displayTubelineStatus(event.items)
                    is TubelineStatusEvent.ShowError -> showErrorMessage(event.errorCode)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun displayTubelineStatus(items: MutableList<TubelineStatusResponse>) {
        Log.d("TubelineStatusActivity", "ShowItems $items")
        UiModelMapper(items)
        tubelineStatusAdapter.notifyDataSetChanged()
    }

    private fun UiModelMapper(items: MutableList<TubelineStatusResponse>) {
        tubelineStatusList.clear()
        for (status in items) {
            tubelineStatusList.add(
                TubelineStatusUiModel(
                    mapColorCode(status.id),
                    status.name,
                    status.lineStatuses.get(0).statusSeverityDescription
                )
            )
        }
    }

    private fun showErrorMessage(errorCode: Int) {
        showToast("Error $errorCode")
    }

    private fun showToast(response: String) {
        Toast.makeText(this, response, Toast.LENGTH_LONG).show()
    }

    private fun mapColorCode(tubelineId: String): Int {
        val colorCodeMap: Map<String,Int> = mapOf<String,Int>(
            "bakerloo" to resources.getColor(R.color.bakerloo),
            "central" to resources.getColor(R.color.central),
            "circle" to resources.getColor(R.color.circle),
            "district" to resources.getColor(R.color.district),
            "hammersmith-city" to resources.getColor(R.color.hammersmith_city),
            "jubilee" to resources.getColor(R.color.jubilee),
            "metropolitan" to resources.getColor(R.color.metropolitan),
            "northern" to resources.getColor(R.color.northern),
            "piccadilly" to resources.getColor(R.color.piccadilly),
            "victoria" to resources.getColor(R.color.victoria),
            "waterloo-city" to resources.getColor(R.color.waterloo_city)
        )
        colorCodeMap[tubelineId]
        return colorCodeMap[tubelineId] ?: resources.getColor(R.color.white)
    }

    override fun onStart() {
        super.onStart()
        observeViewState()
        observeEvents()
    }

    override fun onStop() {
        super.onStop()
        stateJob?.cancel()
        eventJob?.cancel()
    }
}