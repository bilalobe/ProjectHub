package com.projecthub.compose.repository

import com.projecthub.base.student.api.dto.StudentDTO
import com.projecthub.ui.student.repository.StudentRepository
import com.projecthub.ui.student.repository.SubmissionDTO
import com.projecthub.ui.student.repository.SubmissionRepository
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.UUID

/**
 * Ktor implementation of StudentRepository and SubmissionRepository interfaces
 * that works across all platforms using the platform-specific HTTP client.
 */
class KtorStudentRepository(
    private val httpClient: HttpClient,
    private val baseUrl: String
) : StudentRepository, SubmissionRepository {

    // StudentRepository implementation
    
    override suspend fun getAllStudents(): List<StudentDTO> {
        val response = httpClient.get("$baseUrl/api/students")
        return response.body()
    }

    override suspend fun getStudentById(id: UUID): StudentDTO {
        val response = httpClient.get("$baseUrl/api/students/$id")
        return response.body()
    }

    override suspend fun createStudent(student: StudentDTO): StudentDTO {
        val response = httpClient.post("$baseUrl/api/students") {
            contentType(ContentType.Application.Json)
            setBody(student)
        }
        return response.body()
    }

    override suspend fun updateStudent(id: UUID, student: StudentDTO): StudentDTO {
        val response = httpClient.put("$baseUrl/api/students/$id") {
            contentType(ContentType.Application.Json)
            setBody(student)
        }
        return response.body()
    }

    override suspend fun deleteStudent(id: UUID) {
        httpClient.delete("$baseUrl/api/students/$id")
    }
    
    // SubmissionRepository implementation
    
    override suspend fun getSubmissionsByStudentId(studentId: UUID): List<SubmissionDTO> {
        val response = httpClient.get("$baseUrl/api/students/$studentId/submissions")
        return response.body()
    }

    override suspend fun gradeSubmission(submissionId: UUID, grade: Int): SubmissionDTO {
        val response = httpClient.patch("$baseUrl/api/submissions/$submissionId/grade") {
            contentType(ContentType.Application.Json)
            setBody(mapOf("grade" to grade))
        }
        return response.body()
    }

    override suspend fun submitWork(submission: SubmissionDTO): SubmissionDTO {
        val response = httpClient.post("$baseUrl/api/submissions") {
            contentType(ContentType.Application.Json)
            setBody(submission)
        }
        return response.body()
    }
}