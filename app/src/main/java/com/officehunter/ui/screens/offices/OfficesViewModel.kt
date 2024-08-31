package com.officehunter.ui.screens.offices

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.officehunter.data.database.Office
import com.officehunter.data.repositories.OfficesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OfficeRepositoryData(
    val favoriteOffice: Office? = null,
    val otherOffices:List<Office> = emptyList(),
)
data class OfficesViewState(
    val officeToShow: Office? = null
)

data class OfficesState (
    val favoriteOffice: Office? = null,
    val otherOffices:List<Office> = emptyList(),
    val officeToShow: Office? = null
)

interface OfficesActions{
    fun setFavoriteOffice(office: Office): Job
    fun showOfficePosition(office: Office)
    fun closePositionDialog()
}
class OfficesViewModel(
    officesRepository: OfficesRepository
) : ViewModel() {
    private val _viewState= MutableStateFlow(OfficesViewState())
    private val _repositoryData: StateFlow<OfficeRepositoryData> = combine(
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
        OfficeRepositoryData(favoriteOffice = favoriteOffice, otherOffices = otherOffices)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = OfficeRepositoryData()
    )

    val state: StateFlow<OfficesState> = combine(
        _viewState,
        _repositoryData
    ){ viewState, repositoryData ->
        OfficesState(favoriteOffice = repositoryData.favoriteOffice, otherOffices = repositoryData.otherOffices, officeToShow = viewState.officeToShow)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = OfficesState()
    )

    val actions = object : OfficesActions {
        override fun setFavoriteOffice(office: Office) =  viewModelScope.launch  {
            officesRepository.setFavoriteOffice(office)
        }

        override fun showOfficePosition(office: Office) {
            _viewState.update { it.copy(officeToShow = office) }
        }

        override fun closePositionDialog() {
            _viewState.update { it.copy(officeToShow = null) }
        }
    }

    init {
        /*
        if (state.value.favoriteOffice == null){
            viewModelScope.launch{
                officesRepository.setFavoriteOffice(defaultOffices.first())
            }
        }
        */
    }
}

