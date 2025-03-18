package com.projecthub.compose.repository

import com.projecthub.ui.task.model.TaskPriority
import com.projecthub.ui.task.model.TaskStatus
import com.projecthub.ui.task.repository.AttachmentDto
import com.projecthub.ui.task.repository.TaskDto
import com.projecthub.ui.task.repository.TaskRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.util.UUID

/**
 * Shared Ktor-based implementation of TaskRepository that works across all platforms.
 * This implementation uses the platform-specific HTTP client directly.
 */
class KtorTaskRepository(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : TaskRepository {

    override suspend fun getAllTasks(): List<TaskDto> {
        val response = httpClient.get("$baseUrl/api/tasks")
        return response.body()
    }

    override suspend fun getTasksByProject(projectId: UUID): List<TaskDto> {
        val response = httpClient.get("$baseUrl/api/projects/$projectId/tasks")
        return response.body()
    }

    override suspend fun getTaskById(id: UUID): TaskDto {
        val response = httpClient.get("$baseUrl/api/tasks/$id")
        return response.body()
    }

    override suspend fun createTask(
        projectId: UUID,
        name: String,
        description: String,
        priority: TaskPriority,
        assignee: String?,
        deadline: String?,
        tags: List<String>
    ): TaskDto {
        val response = httpClient.post("$baseUrl/api/tasks") {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "projectId" to projectId.toString(),
                "name" to name,
                "description" to description,
                "priority" to priority.name,
                "assignee" to assignee,
                "deadline" to deadline,
                "tags" to tags
            ))
        }
        return response.body()
    }

    override suspend fun updateTask(
        id: UUID,
        name: String?,
        description: String?,
        priority: TaskPriority?,
        assignee: String?,
        deadline: String?,
        tags: List<String>?
    ): TaskDto {
        val params = mutableMapOf<String, Any?>()
        if (name != null) params["name"] = name
        if (description != null) params["description"] = description
        if (priority != null) params["priority"] = priority.name
        if (assignee != null) params["assignee"] = assignee
        if (deadline != null) params["deadline"] = deadline
        if (tags != null) params["tags"] = tags
        
        val response = httpClient.put("$baseUrl/api/tasks/$id") {
            contentType(ContentType.Application.Json)
            setBody(params)
        }
        return response.body()
    }

    override suspend fun updateTaskStatus(taskId: UUID, status: TaskStatus): TaskDto {
        val response = httpClient.patch("$baseUrl/api/tasks/$taskId/status") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("status" to status.name))
        }
        return response.body()
    }

    override suspend fun deleteTask(id: UUID) {
        httpClient.delete("$baseUrl/api/tasks/$id")
    }

    override suspend fun addAttachment(
        taskId: UUID,
        name: String,
        type: String,
        data: ByteArray
    ): AttachmentDto {
        val response = httpClient.submitFormWithBinaryData(
            url = "$baseUrl/api/tasks/$taskId/attachments",
            formData = formData {
                append("name", name)
                append("type", type)
                append("file", data, Headers.build {
                    append(HttpHeaders.ContentType, type)
                    append(HttpHeaders.ContentDisposition, "filename=$name")
                })
            }
        )
        return response.body()
    }

    override suspend fun removeAttachment(taskId: UUID, attachmentId: UUID) {
        httpClient.delete("$baseUrl/api/tasks/$taskId/attachments/$attachmentId")
    }

    override suspend fun addComment(taskId: UUID, text: String) {
        httpClient.post("$baseUrl/api/tasks/$taskId/comments") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("text" to text))
        }
    }
}