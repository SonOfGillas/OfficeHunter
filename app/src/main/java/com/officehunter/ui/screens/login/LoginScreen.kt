package com.officehunter.ui.screens.login

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.officehunter.R
import com.officehunter.ui.composables.AppButton
import com.officehunter.ui.composables.AppTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    state: LoginState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
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
            onValueChange = onEmailChanged,
            label = "Email",
        )
        Spacer(modifier = Modifier.size(12.dp))
        AppTextField(
            value = state.password,
            onValueChange = onPasswordChanged,
            label = "Password",
        )
        Spacer(modifier = Modifier.size(36.dp))
        AppButton(label = "Login",onClick = {})
        Spacer(modifier = Modifier.size(24.dp))
        Row {
            Text(
                "Don't Have an account?",
                color =  MaterialTheme.colorScheme.primary
            )
            Spacer(modifier =  Modifier.size(2.dp))
            Text(
                "Sign Up",
                color =  MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

        }
    }
}