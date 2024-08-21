package com.officehunter.ui.screens.offices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.database.Office
import com.officehunter.data.repositories.OfficesRepository
import com.officehunter.ui.PlacesState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class OfficesState(
    val favoriteOffice: Office? = null,
    val otherOffices:List<Office> = emptyList(),
)

interface OfficesActions{
    suspend fun setFavoriteOffice(office: Office)
}
class OfficesViewModel(
    officesRepository: OfficesRepository
) : ViewModel() {

    val state: StateFlow<OfficesState> = combine(
        officesRepository.offices,
        officesRepository.favoriteOfficeId
    ) { offices, favoriteOfficeId ->
        val favoriteOffice = offices.find { it.officeId == favoriteOfficeId }
        OfficesState(favoriteOffice = favoriteOffice, otherOffices = offices)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = OfficesState()
    )

    val actions = object : OfficesActions {
        override suspend fun setFavoriteOffice(office: Office) {
            officesRepository.setFavoriteOffice(office)
        }
    }
}