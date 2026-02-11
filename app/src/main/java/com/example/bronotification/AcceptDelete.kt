package com.example.bronotification


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DeleteGroupDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    group_id: Int
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Are you sure to delete the group")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.deleteGroup(group_id)
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
