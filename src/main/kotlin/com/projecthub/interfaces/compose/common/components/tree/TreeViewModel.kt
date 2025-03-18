package com.projecthub.ui.components.tree

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.projecthub.ui.components.tree.model.TreeItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

abstract class TreeViewModel<T> {
    private val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    var rootItem by mutableStateOf<TreeItem<T>?>(null)
        protected set
        
    var selectedItem by mutableStateOf<TreeItem<T>?>(null)
        protected set

    fun onItemSelected(item: TreeItem<T>) {
        selectedItem = item
    }

    open fun loadChildren(item: TreeItem<T>) {
        viewModelScope.launch {
            try {
                doLoadChildren(item)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            try {
                loadRoot()
            } catch (e: Exception) {
                onError(e)
            }
        }
        selectedItem = null
    }

    protected abstract suspend fun loadRoot()
    
    protected abstract suspend fun doLoadChildren(item: TreeItem<T>)
    
    protected open fun onError(error: Throwable) {
        // Override to handle errors
    }
}