package com.tfl.tubelinestatus.di

import com.tfl.tubelinestatus.network.TubelineStatusDataSource
import com.tfl.tubelinestatus.network.TubelineStatusHttpClient
import com.tfl.tubelinestatus.network.TubelineStatusService
import com.tfl.tubelinestatus.repositories.api.TubelineStatusRepository
import com.tfl.tubelinestatus.repositories.api.TubelineStatusRepositoryImpl
import com.tfl.tubelinestatus.ui.TubelineStatusViewModel
import com.tfl.tubelinestatus.utils.NetworkResponseCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val TUBELINE_STATUS_URL = "https://api.tfl.gov.uk/Line/Mode/Tube/Status"
const val IO = "IO"

val appModule = module() {
    factory<CoroutineDispatcher>(named(IO)) {
        Dispatchers.IO
    }
    single {
        TubelineStatusHttpClient()
    }

    single {
        NetworkResponseCode()
    }

    viewModel {
        TubelineStatusViewModel(get(), get(named(IO)))
    }

    single {
        TubelineStatusHttpClient().getClient()
    }

    single {
        TubelineStatusService(get(), TUBELINE_STATUS_URL)
    }

    single {
        TubelineStatusDataSource(get(), get())
    }

    single<TubelineStatusRepository> {
        TubelineStatusRepositoryImpl(get())
    }
}