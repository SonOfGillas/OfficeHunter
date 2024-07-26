package com.officehunter.ui.screens.hunt

import androidx.lifecycle.ViewModel
import com.officehunter.ui.screens.stats.StatsActions

interface HuntActions{

}
class HuntViewModel : ViewModel() {
    val actions = object : HuntActions {
    }
}