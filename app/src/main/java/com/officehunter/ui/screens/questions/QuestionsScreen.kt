package com.officehunter.ui.screens.questions

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.officehunter.R
import com.officehunter.ui.composables.AppButton
import com.officehunter.ui.composables.AppTextField

@Composable
fun QuestionsScreen(
    state: QuestionsState,
    actions: QuestionsActions,
    navController: NavHostController
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
                "Office Hunter",
                fontSize = 20.sp,
                color =  MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.size(24.dp))
            Text(
                "Login to your account",
                fontSize = 16.sp,
                color =  MaterialTheme.colorScheme.primary
            )
            /*
            Spacer(modifier = Modifier.size(12.dp))
            AppTextField(
                value = state.email,
                onValueChange = actions::setEmail,
                label = "Email",
            )
            */
            Spacer(modifier = Modifier.size(12.dp))
            AppTextField(
                value = state.residenceCityName,
                onValueChange = actions::setResidenceCityName,
                label = "Residence city name",
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