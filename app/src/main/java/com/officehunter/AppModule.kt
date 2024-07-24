package com.officehunter

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.officehunter.data.database.TravelDiaryDatabase
import com.officehunter.data.remote.FirebaseAuthRemote
import com.officehunter.data.remote.firestore.Firestore
import com.officehunter.data.remote.OSMDataSource
import com.officehunter.data.repositories.PlacesRepository
import com.officehunter.data.repositories.ProfileRepository
import com.officehunter.data.repositories.SettingsRepository
import com.officehunter.data.repositories.UserRepository
import com.officehunter.ui.screens.addtravel.AddTravelViewModel
import com.officehunter.ui.screens.settings.SettingsViewModel
import com.officehunter.utils.LocationService
import com.officehunter.ui.PlacesViewModel
import com.officehunter.ui.screens.login.LoginViewModel
import com.officehunter.ui.screens.profile.ProfileViewModel
import com.officehunter.ui.screens.questions.QuestionsViewModel
import com.officehunter.ui.screens.signUp.SignUpViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Context.dataStore by preferencesDataStore("settings")

val appModule = module {
    single { get<Context>().dataStore }

    single { FirebaseAuthRemote() }

    single { Firestore() }

    single {
        Room.databaseBuilder(
            get(),
            TravelDiaryDatabase::class.java,
            "office-hunter"
        )
            // Sconsigliato per progetti seri! Lo usiamo solo qui per semplicità
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    //Services
    single { OSMDataSource(get()) }
    single { LocationService(get()) }

    //Repository
    single { SettingsRepository(get()) }
    single { ProfileRepository(get()) }
    single {
        UserRepository(get(),get())
    }
    single {
        PlacesRepository(
            get<TravelDiaryDatabase>().placesDAO(),
            get<Context>().applicationContext.contentResolver
        )
    }

    viewModel { AddTravelViewModel() }

    viewModel { SettingsViewModel(get()) }

    viewModel { LoginViewModel(get()) }

    viewModel { PlacesViewModel(get()) }

    viewModel { SignUpViewModel(get()) }

    viewModel { QuestionsViewModel(get()) }

    viewModel { ProfileViewModel(get(),get()) }

}
