package com.officehunter.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

enum class AppTextFieldPreset{
    COLLECT,
    SEARCH,
    DATE
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType? = null,
    preset: AppTextFieldPreset = AppTextFieldPreset.COLLECT,
    onTrailIconPress: (()->Unit)? = null
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val presetColor = when(preset){
        AppTextFieldPreset.COLLECT -> MaterialTheme.colorScheme.primary
        AppTextFieldPreset.SEARCH -> MaterialTheme.colorScheme.tertiary
        AppTextFieldPreset.DATE -> MaterialTheme.colorScheme.primary
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        readOnly = preset == AppTextFieldPreset.DATE,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = presetColor,
            unfocusedBorderColor = presetColor,
            focusedLabelColor = presetColor,
            unfocusedLabelColor = presetColor,
            focusedTextColor = presetColor,
            unfocusedTextColor = presetColor
        ),
        shape = RoundedCornerShape(24.dp),
        visualTransformation = if (!passwordVisible && (keyboardType == KeyboardType.Password)) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType ?: KeyboardType.Text),
        leadingIcon = {
            if(preset == AppTextFieldPreset.SEARCH){
                Icon(imageVector  = Icons.Filled.Search, "", tint = presetColor)
            }
        },
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            val description = if (passwordVisible) "Hide password" else "Show password"

            if(keyboardType == KeyboardType.Password)
                IconButton(onClick = {passwordVisible = !passwordVisible}){
                    Icon(imageVector  = image, description, tint=presetColor)
                }
            if (preset == AppTextFieldPreset.DATE){
                IconButton(onClick = {onTrailIconPress?.let { it() }}) {
                    Icon(imageVector = Icons.Default.DateRange,"", tint = presetColor)
                }
            }
        }

    )
}