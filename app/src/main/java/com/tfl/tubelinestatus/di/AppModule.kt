package com.tfl.tubelinestatus.di

import com.tfl.tubelinestatus.network.TubelineStatusDataSource
import com.tfl.tubelinestatus.network.TubelineStatusHttpClient
import com.tfl.tubelinestatus.network.TubelineStatusService
import com.tfl.tubelinestatus.repositories.api.TubelineStatusRepository
import com.tfl.tubelinestatus.repositories.api.TubelineStatusRepositoryImpl
import com.tfl.tubelinestatus.ui.UIModelMapper
import com.tfl.tubelinestatus.ui.TubelineStatusViewModel
import com.tfl.tubelinestatus.ui.UIErrorMapper
import com.tfl.tubelinestatus.utils.NetworkErrorParser
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val TUBELINE_STATUS_URL = "https://api.tfl.gov.uk/Line/Mode/Tube/Status"
const val IO = "IO"

val appModule = module() {

    //Data
    single {
        TubelineStatusHttpClient()
    }

    single {
        TubelineStatusHttpClient().getClient()
    }

    single {
        NetworkErrorParser()
    }

    single {
        TubelineStatusService(
            httpClient = get(),
            endpoint = TUBELINE_STATUS_URL
        )
    }

    single {
        TubelineStatusDataSource(
            tubelineStatusService = get(),
            networkErrorParser = get()
        )
    }

    //Domain
    single<TubelineStatusRepository> {
        TubelineStatusRepositoryImpl(
            tubelineStatusDataSource = get()
        )
    }

    // UI
    factory(named(IO)) {
        Dispatchers.IO
    }

    single {
        UIErrorMapper(
            resources = androidContext().resources
        )
    }

    single {
        UIModelMapper(
            resources = androidContext().resources
        )
    }

    viewModel {
        TubelineStatusViewModel(
            repository = get(),
            dispatcher = get(named(IO)),
            uiModelMapper = get(),
            uiErrorMapper = get()
        )
    }
}