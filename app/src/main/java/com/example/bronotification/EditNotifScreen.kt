package com.example.bronotification


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bronotification.ui.theme.Light_Black
import com.example.bronotification.ui.theme.Light_Blue

@Composable
fun EditReminderForm(
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    viewModel: MainViewModel,
    groupId: Int,
    notification: Notification,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }


    var title by remember { mutableStateOf(notification.title) }

    // дни недели
    var mo by remember { mutableStateOf(notification.mo) }
    var tu by remember { mutableStateOf(notification.tu) }
    var we by remember { mutableStateOf(notification.we) }
    var th by remember { mutableStateOf(notification.th) }
    var fr by remember { mutableStateOf(notification.fr) }
    var sa by remember { mutableStateOf(notification.sa) }
    var su by remember { mutableStateOf(notification.su) }

    var isPeriodMode by remember { mutableStateOf(notification.isSingle != 1) }

    // время
    var singleHour by remember {
        mutableStateOf(notification.start_time / 60)
    }
    var singleMinute by remember {
        mutableStateOf(notification.start_time % 60)
    }

    var fromHour by remember {
        mutableStateOf(notification.start_time / 60)
    }
    var fromMinute by remember {
        mutableStateOf(notification.start_time % 60)
    }

    var toHour by remember {
        mutableStateOf(notification.end_time / 60)
    }
    var toMinute by remember {
        mutableStateOf(notification.end_time % 60)
    }


    var periodEvery by remember { mutableStateOf(notification.period.toString()) }

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

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier= Modifier
                .fillMaxWidth()
        ){
            Text("Edit notification", style = MaterialTheme.typography.headlineSmall)

            IconButton(onClick = {showDeleteDialog = true }) {
                Icon(
                    painter = painterResource(R.drawable.delete_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
            if (showDeleteDialog) {
                DeleteNotifDialog(
                    viewModel = viewModel,
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = {
                        showDeleteDialog = false
                        onCancel()
                    },
                    notification_id = notification.notification_id
                )
            }

        }

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

                        viewModel.editNotification(
                            Notification(
                                notification_id = notification.notification_id,
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
                Text("Save")
            }

        }
    }

/* ---------- TIME PICKERS ----------*/


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

