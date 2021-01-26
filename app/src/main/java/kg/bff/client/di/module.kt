package kg.bff.client.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kg.bff.client.data.local.SharedPref
import kg.bff.client.data.repository.BookingRepository
import kg.bff.client.data.repository.StadiumsRepository
import kg.bff.client.data.repository.StorageRepository
import kg.bff.client.data.repository.UserRepository
import kg.bff.client.ui.auth.AuthViewModel
import kg.bff.client.ui.map.MapViewModel
import kg.bff.client.ui.requests.list.RequestsViewModel
import kg.bff.client.ui.profile.ProfileViewModel
import kg.bff.client.ui.requests.detail.RequestDetailViewModel
import kg.bff.client.ui.stadium.booking.StadiumBookingListViewModel
import kg.bff.client.ui.stadium.detail.StadiumDetailViewModel
import kg.bff.client.ui.stadium.list.StadiumListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val module: Module = module {
    factory { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }
    single { FirebaseAuth.getInstance() }

    single { SharedPref(androidContext()) }

    // repositories
    single { UserRepository(get()) }
    single { StadiumsRepository(get()) }
    single { StorageRepository(get()) }
    single { BookingRepository(get(),get()) }

    // viewModels
    viewModel { AuthViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(),get()) }
    viewModel { (stadiumId: String) -> StadiumDetailViewModel(stadiumId, get(), get()) }
    viewModel { StadiumListViewModel(get()) }
    viewModel { (stadiumId: String, date: String) -> StadiumBookingListViewModel(stadiumId, date, get()) }
    viewModel { MapViewModel(get()) }
    viewModel { RequestsViewModel(get()) }
    viewModel { RequestDetailViewModel(get()) }
}