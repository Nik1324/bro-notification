package com.example.bronotification

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val groupList by viewModel.groupList.collectAsState(initial = emptyList())
    val notificationList by viewModel.notificationList.collectAsState(initial = emptyList())
    val groupWithNotifications by viewModel.groupWithNotifications.collectAsState(initial = emptyList())


    var isAddGroup by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ){

        LazyColumn {
            items(groupWithNotifications) { groupWithNotifs ->
                GroupCard(groupWithNotifs = groupWithNotifs, viewModel)
            }
            item(){
                Spacer(modifier = Modifier.padding(34.dp))
            }

        }
        if (isAddGroup) {
            CreateGroupDialog(viewModel, { isAddGroup = !isAddGroup })
        }
        FloatingActionButton(
            onClick = { isAddGroup = !isAddGroup },
            containerColor  = MaterialTheme.colorScheme.tertiaryContainer,
            contentColor  = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add"
            )
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupCard(groupWithNotifs: GroupWithNotifications, viewModel: MainViewModel) {
    val groupId = groupWithNotifs.group.group_id
    val isEnable by viewModel.getEnableState(groupId).collectAsState(initial = null)
    var isAddNotif by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showDeleteDialog by remember { mutableStateOf(false) }
    Card(
        colors = if (isEnable == true) {
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        } else {
            CardDefaults.cardColors()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(
                if (isEnable == true) {
                    Modifier
                    //.border(2.dp, Color(0xFF63C9F9), RoundedCornerShape(12.dp))
                    //.background(Color(0xbdeaf3))


                } else {
                    Modifier // без изменений
                }
            ),
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
            // Заголовок группы
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically

            ) {
                    Text(
                        text = groupWithNotifs.group.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        isEnable?.let {
                            viewModel.setEnableState(
                                id = groupId,
                                isEnabled = !it
                            )
                        }
                    }) {
                        if (isEnable == true) {
                            Icon(
                                painter = painterResource(R.drawable.check_box_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
                        } else {
                            Icon(
                                painter = painterResource(R.drawable.check_box_outline_blank_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                    }
                    Text(
                        text = "Enable",
                        color = MaterialTheme.colorScheme.tertiary
                    )



                }

            }
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = { isAddNotif = !isAddNotif }) {
                    Icon(
                        painter = painterResource(R.drawable.add_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(32.dp)
                    )
                }
/*
                IconButton(onClick = { viewModel.deleteGroup(groupId) }) {
                    Icon(
                        painter = painterResource(R.drawable.delete_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                }*/

                IconButton(onClick = {showDeleteDialog = true }) {
                    Icon(
                        painter = painterResource(R.drawable.delete_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                }
                if (showDeleteDialog) {
                    DeleteGroupDialog(
                        viewModel = viewModel,
                        onDismiss = { showDeleteDialog = false },
                        group_id = groupId
                    )
                }

            }
            HorizontalDivider(
                thickness = 2.dp,
                color = Color.LightGray,
            )
            Spacer(modifier = Modifier.padding(bottom = 2.dp))

            // Список уведомлений
            groupWithNotifs.notifications.forEach { notification ->
                val isSingle by viewModel.getSingleState(notification.notification_id).collectAsState(initial = null)
                var isEditNotif by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 6.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 2.dp),
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically

                        ) {
                            Text(
                                text = notification.title,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary

                            )
                            IconButton(onClick = {isEditNotif = !isEditNotif}) {
                                Icon(
                                    painter = painterResource(R.drawable.more_horiz_24dp_e3e3e3_fill0_wght400_grad0_opsz24),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.tertiary,
                                )
                            }
                        }
                        Text(
                            text = buildAnnotatedString {
                                @Composable
                                fun day(label: String, enabled: Boolean) {
                                    withStyle(
                                        style = SpanStyle(color = if (enabled) MaterialTheme.colorScheme.tertiary else Color.LightGray)
                                    ) {
                                        append("$label ")
                                    }
                                }

                                day("Mo", notification.mo)
                                day("Tu", notification.tu)
                                day("We", notification.we)
                                day("Th", notification.th)
                                day("Fr", notification.fr)
                                day("Sa", notification.sa)
                                day("Su", notification.su)
                            }
                        )

                        if (isSingle == true) {
                            Text(
                                text = "At: ${minutesToTime(notification.start_time)}",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 0.dp)
                            )
                        } else {
                            Text(
                                text = "From: ${minutesToTime(notification.start_time)} " +
                                        "to: ${minutesToTime(notification.end_time)} " +
                                        "period: ${notification.period} min",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 0.dp)
                            )
                        }
                        HorizontalDivider(
                            color = Color.LightGray
                        )

                        if (isEditNotif) {
                            ModalBottomSheet(
                                onDismissRequest = { isEditNotif = false },
                                sheetState = sheetState,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Добавляем прокрутку и немного "воздуха" внизу
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .verticalScroll(rememberScrollState())
                                        .padding(bottom = 32.dp) // чтобы не прилипало к краю
                                ) {
                                    EditReminderForm(
                                        onConfirm = { isEditNotif = false },
                                        onCancel = { isEditNotif = false },
                                        viewModel = viewModel,
                                        groupId = groupId,
                                        notification = notification
                                    )
                                }
                            }
                        }

                    }
                }


            }

        }
    }
    if (isAddNotif) {
        ModalBottomSheet(
            onDismissRequest = { isAddNotif = false },
            sheetState = sheetState,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Добавляем прокрутку и немного "воздуха" внизу
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 32.dp) // чтобы не прилипало к краю
            ) {
                NewReminderForm(
                    onConfirm = { isAddNotif = false },
                    onCancel = { isAddNotif = false },
                    viewModel = viewModel,
                    groupId = groupId,
                )
            }
        }
    }
}

fun minutesToTime(minutes: Int): String {
    val h = minutes / 60
    val m = minutes % 60
    return "%02d:%02d".format(h, m)
}