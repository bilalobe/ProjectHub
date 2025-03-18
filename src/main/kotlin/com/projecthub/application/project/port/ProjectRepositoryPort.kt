package com.projecthub.application.project.port

import com.projecthub.application.project.domain.Project

interface ProjectRepositoryPort {
    fun save(project: Project): Project
    fun findById(id: String): Project?
    fun deleteById(id: String)
}
