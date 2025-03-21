package org.example.expert.domain.manager.log.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "logs")
class Log(var message: String) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var createdAt: LocalDateTime? = null

    init {
        createdAt = LocalDateTime.now()
    }
}