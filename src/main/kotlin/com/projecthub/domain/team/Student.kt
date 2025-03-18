package com.projecthub.domain.team

import com.projecthub.domain.BaseEntity
import com.projecthub.domain.user.UserId
import jakarta.persistence.*

@Entity
@Table(name = "students")
class Student(
    @Column(name = "student_id", nullable = false)
    var studentId: String,

    @Column(name = "user_id", nullable = false)
    var userId: UserId,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    var team: Team? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: StudentStatus = StudentStatus.ACTIVE
) : BaseEntity()

enum class StudentStatus {
    ACTIVE,
    INACTIVE,
    GRADUATED,
    WITHDRAWN
}