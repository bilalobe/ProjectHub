package com.projecthub.ui.data.repository

interface TreeRepository<T> {
    suspend fun getRootItems(): List<T>
    suspend fun getChildren(parent: T): List<T>
}