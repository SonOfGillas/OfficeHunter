package com.officehunter.ui.screens.login

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.officehunter.R
import com.officehunter.data.remote.FirebaseAuth
import com.officehunter.ui.OfficeHunterRoute
import com.officehunter.ui.composables.AppButton
import com.officehunter.ui.composables.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: LoginState,
    actions: LoginActions,
    navController: NavHostController
){
    val snackbarHostState = remember { SnackbarHostState() }

    if(state.loginPhase == LoginPhase.LOGGED){
        navController.navigate(OfficeHunterRoute.Profile.route)
    }

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
            Spacer(modifier = Modifier.size(12.dp))
            AppTextField(
                value = state.email,
                onValueChange = actions::setEmail,
                label = "Email",
            )
            Spacer(modifier = Modifier.size(12.dp))
            AppTextField(
                value = state.password,
                onValueChange = actions::setPassword,
                label = "Password",
                keyboardType = KeyboardType.Password
            )
            Spacer(modifier = Modifier.size(36.dp))
            AppButton(label = "Login",onClick = {actions.login()})
            Spacer(modifier = Modifier.size(24.dp))
            Row {
                Text(
                    "Don't Have an account?",
                    color =  MaterialTheme.colorScheme.primary
                )
                Spacer(modifier =  Modifier.size(2.dp))
                Box(Modifier.clickable {
                    navController.navigate(OfficeHunterRoute.SignUp.route)
                }){
                    Text(
                        "Sign Up",
                        color =  MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

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