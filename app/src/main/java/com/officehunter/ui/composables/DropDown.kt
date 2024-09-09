package com.officehunter.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.ktor.websocket.Frame

data class AppDropDownElement<T>(
    val label: String,
    val value: T
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> AppDropDown(
     selectedElement: T,
     options: List<AppDropDownElement<T>>,
     onSelect: (element: T) -> Unit,
     label: String = ""
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            value = options.find { it.value == selectedElement }?.label ?: "",
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.onBackground,
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground,
                focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
            ),
            shape = RoundedCornerShape(24.dp),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier= Modifier
                .background(MaterialTheme.colorScheme.onSurface)
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = {
                           Text(
                               selectionOption.label,
                               fontWeight = FontWeight.SemiBold,
                               color = MaterialTheme.colorScheme.secondary
                           )
                    },
                    onClick = {
                        onSelect(selectionOption.value)
                        expanded = false
                    },
                )
            }
        }

    }
}