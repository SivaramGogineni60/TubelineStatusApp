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
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class TubelineStatusActivity : AppCompatActivity() {
    private val viewModel by viewModel<TubelineStatusViewModel>()
    lateinit var progress: ProgressBar
    lateinit var recyclerView: RecyclerView

    private var stateJob: Job? = null
    private var eventJob: Job? = null

    private lateinit var tubelineStatusAdapter: TubelineStatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tubeline_status)
        progress = findViewById(R.id.progress)

        setupRecyclerView()
        viewModel.getTubelineStatuses()
    }

    private fun setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(applicationContext)
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        tubelineStatusAdapter = TubelineStatusAdapter()
        recyclerView.adapter = tubelineStatusAdapter
    }

    private fun observeViewState() {
        stateJob = lifecycleScope.launch() {
            viewModel.viewState.collectLatest { viewState ->
                showLoadingStatus(viewState.shouldShowLoadingMessage)
                displayTubelineStatus(viewState.tubelineStatus)
            }
        }
    }

    private fun showLoadingStatus(showLoading: Boolean) {
        progress.isVisible = showLoading
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun displayTubelineStatus(items: List<TubelineStatusUIModel>) {
        Log.d("TubelineStatusActivity", "ShowItems $items")
        tubelineStatusAdapter.setData(items)
        tubelineStatusAdapter.notifyDataSetChanged()
    }

    private fun observeEvents() {
        eventJob = lifecycleScope.launch {
            viewModel.events.collectLatest { event ->
                when (event) {
                    is TubelineStatusEvent.ShowError -> showErrorMessage(event.errorMessage)
                }
            }
        }
    }

    private fun showErrorMessage(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
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