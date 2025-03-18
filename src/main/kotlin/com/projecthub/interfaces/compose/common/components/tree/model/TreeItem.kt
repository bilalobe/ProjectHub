package com.projecthub.ui.components.tree.model

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

@Stable
data class TreeItem<T>(
    val id: String,
    val name: String,
    val data: T?,
    val children: SnapshotStateList<TreeItem<T>> = mutableStateListOf()
) {
    fun addChild(child: TreeItem<T>) {
        children.add(child)
    }

    fun clearChildren() {
        children.clear()
    }
}