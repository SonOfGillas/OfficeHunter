package com.officehunter.ui.screens.offices

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.database.Office
import com.officehunter.data.repositories.OfficesRepository
import com.officehunter.ui.PlacesState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OfficesState(
    val favoriteOffice: Office? = null,
    val otherOffices:List<Office> = emptyList(),
)

interface OfficesActions{
    fun setFavoriteOffice(office: Office): Job
}
class OfficesViewModel(
    officesRepository: OfficesRepository
) : ViewModel() {

    val state: StateFlow<OfficesState> = combine(
        officesRepository.offices,
        officesRepository.favoriteOfficeId
    ) { offices, favoriteOfficeId ->
        var favoriteOffice:Office? = null
        if(favoriteOfficeId != null){
            favoriteOffice = offices.find { it.officeId == favoriteOfficeId }
        }
        var otherOffices: List<Office> = offices
        if(favoriteOffice!=null){
            otherOffices = otherOffices.filter { office -> office.officeId != favoriteOfficeId   }
        }
        OfficesState(favoriteOffice = favoriteOffice, otherOffices = otherOffices)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = OfficesState()
    )

    val actions = object : OfficesActions {
        override fun setFavoriteOffice(office: Office) =  viewModelScope.launch  {
            officesRepository.setFavoriteOffice(office)
        }
    }

    init {
        if (state.value.favoriteOffice == null && state.value.otherOffices.isEmpty()){
            for (office in defaultOffices){
                viewModelScope.launch {
                    officesRepository.upsertOffice(office)
                }
            }
            viewModelScope.launch {
                officesRepository.setFavoriteOffice(defaultOffices.first())
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