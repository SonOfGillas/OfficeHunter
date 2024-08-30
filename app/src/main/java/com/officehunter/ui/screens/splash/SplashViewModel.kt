package com.officehunter.ui.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.officehunter.data.database.Office
import com.officehunter.data.repositories.OfficesRepository
import com.officehunter.ui.screens.offices.OfficesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

enum class SplashState{
    Loading,
    Ready
}

class SplashViewModel(
    officesRepository: OfficesRepository
): ViewModel(){
    private val _state = MutableStateFlow(SplashState.Loading)
    val state = _state.asStateFlow()

    private val officeRepositoryData:StateFlow<List<Office>> = officesRepository.offices.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            withTimeout(1000){
                if (officeRepositoryData.value.isEmpty()){
                    for (office in defaultOffices){
                        viewModelScope.launch {
                            officesRepository.upsertOffice(office)
                        }
                    }
                }
                _state.update { SplashState.Ready }
            }
        }
    }
}

val defaultOffices = listOf(
    Office(
        officeId = 1,
        name = "San patrizio",
        street = "via g.dalle vacche 33",
        latitude = 44.495083,
        longitude = 11.832050
    ),
    Office(
        officeId = 2,
        name = "Lugo",
        street = "via piano caricatore 9",
        latitude = 44.414139,
        longitude = 11.916125
    ),
    Office(
        officeId = 3,
        name = "Imola",
        street = "via selice 51",
        latitude = 44.380724,
        longitude = 11.739033
    ),
    Office(
        officeId = 4,
        name = "Cesena University",
        street = "via cesare pavese 50",
        latitude = 44.148357,
        longitude = 12.235488
    )
)