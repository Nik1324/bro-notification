package com.example.bronotification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.bronotification.ui.theme.Light_Black
import com.example.bronotification.ui.theme.Light_Blue

@Composable
fun NewReminderForm(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    viewModel: MainViewModel,
    groupId: Int,
    modifier: Modifier = Modifier
) {


    var title by remember { mutableStateOf("") }

    // дни недели
    var mo by remember { mutableStateOf(false) }
    var tu by remember { mutableStateOf(false) }
    var we by remember { mutableStateOf(false) }
    var th by remember { mutableStateOf(false) }
    var fr by remember { mutableStateOf(false) }
    var sa by remember { mutableStateOf(false) }
    var su by remember { mutableStateOf(false) }

    var isPeriodMode by remember { mutableStateOf(false) }

    // время
    var singleHour by remember { mutableStateOf(9) }
    var singleMinute by remember { mutableStateOf(0) }

    var fromHour by remember { mutableStateOf(9) }
    var fromMinute by remember { mutableStateOf(0) }

    var toHour by remember { mutableStateOf(18) }
    var toMinute by remember { mutableStateOf(0) }

    var periodEvery by remember { mutableStateOf("60") }

    // dialogs
    var showSinglePicker by remember { mutableStateOf(false) }
    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("New notification", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Text") },
            modifier = Modifier.fillMaxWidth()
        )


        Text("Days of week")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DayButton("Mo", mo) { mo = !mo }
            DayButton("Tu", tu) { tu = !tu }
            DayButton("We", we) { we = !we }
            DayButton("Th", th) { th = !th }
            DayButton("Fr", fr) { fr = !fr }
            DayButton("Sa", sa) { sa = !sa }
            DayButton("Su", su) { su = !su }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { isPeriodMode = false },
                colors = if (!isPeriodMode)
                    ButtonDefaults.buttonColors()
                else ButtonDefaults.outlinedButtonColors()
            ) {
                Text("Single")
            }

            Button(
                onClick = { isPeriodMode = true },
                colors = if (isPeriodMode)
                    ButtonDefaults.buttonColors()
                else ButtonDefaults.outlinedButtonColors()
            ) {
                Text("Period")
            }
        }

        if (!isPeriodMode) {
            TimeField(
                label = "Time",
                hour = singleHour,
                minute = singleMinute,
                onClick = { showSinglePicker = true }
            )
        } else {
            TimeField(
                label = "From",
                hour = fromHour,
                minute = fromMinute,
                onClick = { showFromPicker = true }
            )

            TimeField(
                label = "To",
                hour = toHour,
                minute = toMinute,
                onClick = { showToPicker = true }
            )
            OutlinedTextField(
                value = periodEvery,
                onValueChange = { newValue ->
                    if (newValue.all(Char::isDigit)) {
                        periodEvery = newValue
                    }
                },
                label = { Text("Every (min)") },
                modifier = Modifier.fillMaxWidth()
            )

        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    if (title.isNotBlank()) {

                        val startTime: Int
                        val endTime: Int
                        val period: Int
                        val isSingleValue: Int

                        if (isPeriodMode) {
                            startTime = timeToMinutes(fromHour, fromMinute)
                            endTime = timeToMinutes(toHour, toMinute)
                            period = periodEvery.toIntOrNull() ?: 60
                            isSingleValue = 0
                        } else {
                            startTime = timeToMinutes(singleHour, singleMinute)
                            endTime = startTime
                            period = 24*60
                            isSingleValue = 1
                        }

                        viewModel.addNotification(
                            Notification(
                                group_id = groupId,
                                title = title,
                                mo = mo,
                                tu = tu,
                                we = we,
                                th = th,
                                fr = fr,
                                sa = sa,
                                su = su,
                                start_time = startTime,
                                end_time = endTime,
                                period = period,
                                isSingle = isSingleValue
                            )
                        )

                        onConfirm()
                    }
                }
            ) {
                Text("Create")
            }

        }
    }

    /* ---------- TIME PICKERS ---------- */

    if (showSinglePicker) {
        TimePickerDialog(
            initialHour = singleHour,
            initialMinute = singleMinute,
            onDismiss = { showSinglePicker = false },
            onConfirm = { h, m ->
                singleHour = h
                singleMinute = m
                showSinglePicker = false
            }
        )
    }

    if (showFromPicker) {
        TimePickerDialog(
            initialHour = fromHour,
            initialMinute = fromMinute,
            onDismiss = { showFromPicker = false },
            onConfirm = { h, m ->
                fromHour = h
                fromMinute = m
                showFromPicker = false
            }
        )
    }

    if (showToPicker) {
        TimePickerDialog(
            initialHour = toHour,
            initialMinute = toMinute,
            onDismiss = { showToPicker = false },
            onConfirm = { h, m ->
                toHour = h
                toMinute = m
                showToPicker = false
            }
        )
    }
}

/* ---------- UI HELPERS ---------- */

@Composable
fun DayButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) Color(Light_Blue.value) else Color(Light_Black.value)
        )
    }
}

@Composable
fun TimeField(
    label: String,
    hour: Int,
    minute: Int,
    onClick: () -> Unit
) {
    OutlinedTextField(
        value = "%02d:%02d".format(hour, minute),
        onValueChange = {},
        label = { Text(label) },
        enabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    )
}

/* ---------- MATERIAL 3 TIME PICKER DIALOG ---------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onConfirm(state.hour, state.minute)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = state)
        }
    )
}

fun timeToMinutes(hour: Int, minute: Int): Int {
    return hour * 60 + minute
}