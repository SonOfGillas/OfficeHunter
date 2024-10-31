package com.officehunter.ui.screens.hunted


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.officehunter.data.remote.firestore.entities.Rarity
import com.officehunter.ui.composables.AppDropDown
import com.officehunter.ui.composables.AppDropDownElement
import com.officehunter.ui.composables.RarityBadge
import java.util.EnumSet


@Composable
fun HuntedFilterDialog(actions: HuntedActions, state: HuntedState) {
    if (state.showFilterDialog) {
        Dialog(onDismissRequest = actions::closeFilterDialog, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            // Custom layout for the dialog
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                shape = RoundedCornerShape(0.dp),
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(
                            "Filters",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        IconButton(onClick = actions::closeFilterDialog){
                            Icon(
                                imageVector  = Icons.Filled.Close,
                                "close filter dialog",
                                tint=MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(42.dp)
                                )
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(text = "Show:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    AppCheckBox(label = "Founded", checked = state.filterShowFounded, onClick = actions::clickShowFound )
                    AppCheckBox(label = "Not Founded", checked = state.filterShowNotFounded, onClick = actions::clickShowNotFounded )
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(text = "Order by:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    AppDropDown(
                        selectedElement = state.filterOrderRule,
                        options = EnumSet.allOf(FilterOrderRule::class.java).toList().map { AppDropDownElement(
                            label = it.toString(),
                            value = it
                        )},
                        onSelect = actions::selectOrderRule
                    )
                    AppDropDown(
                        selectedElement = state.filterOrderValue,
                        options = EnumSet.allOf(FilterOrderValue::class.java).toList().map { AppDropDownElement(
                            label = it.toString(),
                            value = it
                        )},
                        onSelect = actions::selectOrderValue
                    )
                    Spacer(modifier = Modifier.size(20.dp))
                    Text(text = "Rarities:",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    RarityBadgeList(
                        state.selectedRarities,
                        actions::toggleRarity
                    )
                }
            }
        }
    }
}

@Composable
fun AppCheckBox(
    label: String,
    checked: Boolean,
    onClick: (checked:Boolean)->Unit
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onClick,
            colors = CheckboxDefaults.colors(
                checkedColor = MaterialTheme.colorScheme.onBackground,
                checkmarkColor = MaterialTheme.colorScheme.background
            )
        )
        Text(
            label,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp
        )
    }
}

@Composable
fun RarityBadgeList(
    selectedRarities: Set<Rarity>,
    onRarityClick: (rarity:Rarity) -> Unit
){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp, 8.dp, 8.dp, 80.dp),
    ) {
        val raritiesList =  EnumSet.allOf(Rarity::class.java).toList()
        items(raritiesList) { rarity ->
            RarityBadge(
                rarity = rarity,
                isSelected = selectedRarities.contains(rarity),
                onPress = onRarityClick,
                fillMaxWidth = true
            )
        }
    }
}