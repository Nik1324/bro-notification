package com.example.bronotification



import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DeleteNotifDialog(
    viewModel: MainViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    notification_id: Int
) {

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Are you sure to delete the notification")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.deleteNotification(notification_id)
                    onConfirm()
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
