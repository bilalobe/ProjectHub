package com.projecthub.ui.components.tree

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.projecthub.ui.components.tree.model.TreeItem

@Composable
fun <T> TreeView(
    modifier: Modifier = Modifier,
    item: TreeItem<T>,
    onLoadChildren: (TreeItem<T>) -> Unit = {},
    onItemSelected: (TreeItem<T>) -> Unit = {},
    isSelected: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val hasChildren = item.children.isNotEmpty()

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemSelected(item) }
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (hasChildren) {
                IconButton(onClick = {
                    expanded = !expanded
                    if (expanded && item.children.isEmpty()) {
                        onLoadChildren(item)
                    }
                }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(40.dp))
            }
            
            Text(
                text = item.name,
                style = MaterialTheme.typography.body1,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
            )
        }
        
        AnimatedVisibility(visible = expanded && hasChildren) {
            Column(modifier = Modifier.padding(start = 16.dp)) {
                item.children.forEach { child ->
                    TreeView(
                        item = child,
                        onLoadChildren = onLoadChildren,
                        onItemSelected = onItemSelected
                    )
                }
            }
        }
    }
}