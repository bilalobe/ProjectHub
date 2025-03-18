package com.projecthub.ui.cohort.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.projecthub.base.cohort.domain.value.PositionedDevice
import com.projecthub.ui.shared.hover.HoverEffect.withConditionalHover
import com.projecthub.ui.shared.hover.HoverEffect.isHovered

/**
 * Component for managing IoT devices on the seating map.
 * 
 * Allows teachers and admins to:
 * - View all devices
 * - Add new devices
 * - Position devices on the map
 * - Control device status
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IoTDeviceManager(
    devices: Map<String, PositionedDevice>,
    userHasManageIoTPermission: Boolean,
    onAddDevice: (PositionedDevice) -> Unit,
    onRemoveDevice: (String) -> Unit,
    onUpdateDevice: (PositionedDevice) -> Unit,
    onControlDevice: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeviceDrawer by remember { mutableStateOf(false) }
    var showAddDeviceDialog by remember { mutableStateOf(false) }
    var selectedDevice by remember { mutableStateOf<PositionedDevice?>(null) }
    
    Box(modifier) {
        // Button to open device drawer
        Button(
            onClick = { showDeviceDrawer = !showDeviceDrawer },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = if (!showDeviceDrawer) Icons.Default.DeviceHub else Icons.Default.Close,
                contentDescription = if (!showDeviceDrawer) "Manage IoT Devices" else "Close"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (!showDeviceDrawer) "IoT Devices" else "Close")
        }
        
        // Device drawer
        AnimatedVisibility(
            visible = showDeviceDrawer,
            enter = fadeIn() + slideInHorizontally { it },
            exit = fadeOut() + slideOutHorizontally { it }
        ) {
            Card(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp, top = 72.dp, bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Classroom IoT Devices",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    // List of devices
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (devices.isEmpty()) {
                            item {
                                Text(
                                    text = "No IoT devices configured",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            items(devices.entries.toList()) { (id, device) ->
                                IoTDeviceItem(
                                    device = device,
                                    userHasManageIoTPermission = userHasManageIoTPermission,
                                    onItemClick = { selectedDevice = device },
                                    onControlClick = { command -> 
                                        onControlDevice(id, command)
                                    },
                                    onRemoveClick = { 
                                        if (userHasManageIoTPermission) {
                                            onRemoveDevice(id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    
                    // Actions footer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                    ) {
                        // Only show add device button if user has permission
                        if (userHasManageIoTPermission) {
                            Button(
                                onClick = { showAddDeviceDialog = true },
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Device")
                            }
                        }
                    }
                }
            }
        }
        
        // Device details dialog
        selectedDevice?.let { device ->
            DeviceDetailsDialog(
                device = device,
                userHasManageIoTPermission = userHasManageIoTPermission,
                onDismiss = { selectedDevice = null },
                onUpdate = { updatedDevice ->
                    onUpdateDevice(updatedDevice)
                    selectedDevice = null
                }
            )
        }
        
        // Add device dialog
        if (showAddDeviceDialog) {
            AddDeviceDialog(
                onDismiss = { showAddDeviceDialog = false },
                onAddDevice = { device ->
                    onAddDevice(device)
                    showAddDeviceDialog = false
                }
            )
        }
    }
}

@Composable
private fun IoTDeviceItem(
    device: PositionedDevice,
    userHasManageIoTPermission: Boolean,
    onItemClick: () -> Unit,
    onControlClick: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    val (hoverModifier, isHovered) = Modifier.isHovered()
    
    // Scale animation on hover
    val scale by animateFloatAsState(
        targetValue = if (isHovered) 1.02f else 1.0f,
        animationSpec = tween(durationMillis = 150)
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale)
            .then(hoverModifier)
            .clickable(onClick = onItemClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isHovered) 
                MaterialTheme.colorScheme.surfaceVariant 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isHovered) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Device icon/indicator
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when (device.deviceType) {
                            "PROJECTOR", "DISPLAY" -> MaterialTheme.colorScheme.primary
                            "SPEAKER" -> MaterialTheme.colorScheme.secondary
                            else -> MaterialTheme.colorScheme.tertiary
                        }
                    )
                    .border(
                        width = if (isHovered) 2.dp else 1.dp,
                        color = if (isHovered) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (device.deviceType) {
                        "PROJECTOR" -> Icons.Default.Movie
                        "DISPLAY" -> Icons.Default.Tv
                        "SPEAKER" -> Icons.Default.VolumeUp
                        "MICROPHONE" -> Icons.Default.Mic
                        "SMART_BOARD" -> Icons.Default.TouchApp
                        else -> Icons.Default.DeviceHub
                    },
                    contentDescription = device.deviceType,
                    tint = Color.White
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Device details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = device.deviceName,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Text(
                    text = "Position: R${device.row + 1}, C${device.column + 1}",
                    style = MaterialTheme.typography.bodySmall
                )
                
                Text(
                    text = "Status: ${if (device.isActive) "Active" else "Inactive"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (device.isActive) 
                        MaterialTheme.colorScheme.primary 
                    else 
                        MaterialTheme.colorScheme.error
                )
                
                // Show additional info on hover
                if (isHovered) {
                    if (!device.ipAddress.isNullOrBlank()) {
                        Text(
                            text = "IP: ${device.ipAddress}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // Action buttons
            Row {
                // Power toggle with hover effect
                val (powerHoverModifier, isPowerHovered) = Modifier.isHovered()
                
                IconButton(
                    onClick = { onControlClick(if (device.isActive) "POWER_OFF" else "POWER_ON") },
                    modifier = powerHoverModifier
                ) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Power,
                            contentDescription = if (device.isActive) "Turn Off" else "Turn On",
                            tint = if (device.isActive) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        
                        // Show tooltip on hover
                        if (isPowerHovered) {
                            Surface(
                                modifier = Modifier
                                    .zIndex(1f)
                                    .width(80.dp)
                                    .align(Alignment.BottomCenter)
                                    .offset(y = 8.dp),
                                shape = RoundedCornerShape(4.dp),
                                tonalElevation = 2.dp,
                                shadowElevation = 2.dp,
                                color = MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (device.isActive) "Turn Off" else "Turn On",
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(4.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                
                // Only show delete button if user has permission
                if (userHasManageIoTPermission) {
                    val (deleteHoverModifier, isDeleteHovered) = Modifier.isHovered()
                    
                    IconButton(
                        onClick = onRemoveClick,
                        modifier = deleteHoverModifier
                    ) {
                        Box {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remove Device",
                                tint = if (isDeleteHovered)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                            )
                            
                            // Show tooltip on hover
                            if (isDeleteHovered) {
                                Surface(
                                    modifier = Modifier
                                        .zIndex(1f)
                                        .width(80.dp)
                                        .align(Alignment.BottomCenter)
                                        .offset(y = 8.dp),
                                    shape = RoundedCornerShape(4.dp),
                                    tonalElevation = 2.dp,
                                    shadowElevation = 2.dp,
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = "Remove Device",
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(4.dp),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceDetailsDialog(
    device: PositionedDevice,
    userHasManageIoTPermission: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (PositionedDevice) -> Unit
) {
    var deviceName by remember { mutableStateOf(device.deviceName) }
    var deviceRow by remember { mutableStateOf(device.row) }
    var deviceColumn by remember { mutableStateOf(device.column) }
    var deviceActive by remember { mutableStateOf(device.isActive) }
    var ipAddress by remember { mutableStateOf(device.ipAddress ?: "") }
    var macAddress by remember { mutableStateOf(device.macAddress ?: "") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Device Details") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (userHasManageIoTPermission) {
                    // Editable fields if user has permission
                    TextField(
                        value = deviceName,
                        onValueChange = { deviceName = it },
                        label = { Text("Device Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = deviceRow.toString(),
                            onValueChange = {
                                deviceRow = it.toIntOrNull() ?: deviceRow
                            },
                            label = { Text("Row") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        
                        OutlinedTextField(
                            value = deviceColumn.toString(),
                            onValueChange = {
                                deviceColumn = it.toIntOrNull() ?: deviceColumn
                            },
                            label = { Text("Column") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    
                    TextField(
                        value = ipAddress,
                        onValueChange = { ipAddress = it },
                        label = { Text("IP Address (optional)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    
                    TextField(
                        value = macAddress,
                        onValueChange = { macAddress = it },
                        label = { Text("MAC Address (optional)") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    
                    // Status toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Device Status:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Switch(
                            checked = deviceActive,
                            onCheckedChange = { deviceActive = it }
                        )
                    }
                } else {
                    // Read-only view if user doesn't have permission
                    DeviceInfoField("Device Name", deviceName)
                    DeviceInfoField("Type", device.deviceType)
                    DeviceInfoField("Position", "Row ${device.row + 1}, Column ${device.column + 1}")
                    DeviceInfoField("Status", if (device.isActive) "Active" else "Inactive")
                    
                    if (!device.ipAddress.isNullOrEmpty()) {
                        DeviceInfoField("IP Address", device.ipAddress)
                    }
                    
                    if (!device.macAddress.isNullOrEmpty()) {
                        DeviceInfoField("MAC Address", device.macAddress)
                    }
                }
            }
        },
        confirmButton = {
            if (userHasManageIoTPermission) {
                Button(
                    onClick = {
                        val updatedDevice = PositionedDevice(
                            device.deviceId,
                            device.deviceType,
                            deviceName,
                            deviceRow,
                            deviceColumn,
                            deviceActive,
                            if (ipAddress.isEmpty()) null else ipAddress,
                            if (macAddress.isEmpty()) null else macAddress
                        )
                        onUpdate(updatedDevice)
                    }
                ) {
                    Text("Save Changes")
                }
            } else {
                Button(onClick = onDismiss) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            if (userHasManageIoTPermission) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
private fun DeviceInfoField(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(120.dp)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun AddDeviceDialog(
    onDismiss: () -> Unit,
    onAddDevice: (PositionedDevice) -> Unit
) {
    var deviceId by remember { mutableStateOf("") }
    var deviceName by remember { mutableStateOf("") }
    var deviceType by remember { mutableStateOf("PROJECTOR") }
    var deviceRow by remember { mutableStateOf(0) }
    var deviceColumn by remember { mutableStateOf(0) }
    var ipAddress by remember { mutableStateOf("") }
    var macAddress by remember { mutableStateOf("") }
    
    val deviceTypes = listOf("PROJECTOR", "DISPLAY", "SPEAKER", "MICROPHONE", "SMART_BOARD", "OTHER")
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add IoT Device") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = deviceId,
                    onValueChange = { deviceId = it },
                    label = { Text("Device ID") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                
                TextField(
                    value = deviceName,
                    onValueChange = { deviceName = it },
                    label = { Text("Device Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                
                // Device type dropdown
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    TextField(
                        value = deviceType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Device Type") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = false)
                        },
                        colors = ExposedDropdownMenuDefaults.textFieldColors(),
                        modifier = Modifier.menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = {}
                    ) {
                        deviceTypes.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = { deviceType = type }
                            )
                        }
                    }
                }
                
                // Position
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = deviceRow.toString(),
                        onValueChange = {
                            deviceRow = it.toIntOrNull() ?: deviceRow
                        },
                        label = { Text("Row") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    
                    OutlinedTextField(
                        value = deviceColumn.toString(),
                        onValueChange = {
                            deviceColumn = it.toIntOrNull() ?: deviceColumn
                        },
                        label = { Text("Column") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
                
                TextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("IP Address (optional)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
                
                TextField(
                    value = macAddress,
                    onValueChange = { macAddress = it },
                    label = { Text("MAC Address (optional)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (deviceId.isNotBlank() && deviceName.isNotBlank()) {
                        val newDevice = PositionedDevice(
                            deviceId,
                            deviceType,
                            deviceName,
                            deviceRow,
                            deviceColumn,
                            true,
                            if (ipAddress.isEmpty()) null else ipAddress,
                            if (macAddress.isEmpty()) null else macAddress
                        )
                        onAddDevice(newDevice)
                    }
                },
                enabled = deviceId.isNotBlank() && deviceName.isNotBlank()
            ) {
                Text("Add Device")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}