package com.officehunter.ui.screens.hunterInfo

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.officehunter.R
import com.officehunter.data.entities.WorkRoles
import com.officehunter.data.entities.getRoleName
import com.officehunter.ui.composables.AppButton
import com.officehunter.ui.composables.AppDropDown
import com.officehunter.ui.composables.AppDropDownElement
import com.officehunter.ui.composables.AppDatePicker
import java.util.Date
import java.util.EnumSet

@Composable
fun QuestionsScreen(
    state: HunterInfoState,
    actions: HunterInfoActions,
){
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold (
        snackbarHost = { SnackbarHost(snackbarHostState){ data ->
            Snackbar(snackbarData = data, containerColor = MaterialTheme.colorScheme.error)
        } },
    ){ contentPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(contentPadding)
                .padding(12.dp)
                .fillMaxSize()
        ){
            val image = painterResource(R.drawable.logov2)
            Image(
                painter = image,
                contentDescription = null,
                modifier = Modifier.width(160.dp)
            )
            Text(
                "Hunter Info",
                fontSize = 20.sp,
                color =  MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.size(24.dp))
            Box(modifier = Modifier.width(260.dp)){
                Text(
                    "Colleting Info needed to generate the Hunter avatars ",
                    fontSize = 16.sp,
                    color =  MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            AppDropDown(
                selectedElement = state.workRole,
                options = EnumSet.allOf(WorkRoles::class.java).toList().map {AppDropDownElement(
                                                                                label = getRoleName(it),
                                                                                value = it
                                                                            )},
                onSelect = actions::setWorkRole,
                label = "work role"
            )
            Spacer(modifier = Modifier.size(12.dp))
            AppDatePicker(
                label = "birthday",
                onPickerClose = {
                    actions.setBirthDay(
                        if(it != null) Date(it) else null
                    )
                }
            )
            Spacer(modifier = Modifier.size(12.dp))
            AppDatePicker(
                label = "hire date",
                onPickerClose = {
                    actions.setHireDate(
                        if(it != null) Date(it) else null
                    )}
            )
            Spacer(modifier = Modifier.size(36.dp))
            AppButton(label = "Next",onClick = {actions.goNext()})
            Spacer(modifier = Modifier.size(24.dp))

            if (state.hasError()) {
                LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = "Error: ${state.errorMessage}",
                        duration = SnackbarDuration.Short
                    )
                    actions.closeError()
                }
            }
        }
    }

}