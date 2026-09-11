package ru.sogaz.site.bff.service.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant
import java.time.LocalDateTime
import java.util.UUID

/**
 * Информационное сообщение для отображения клиенту на платежной странице.
 */
@Entity
@Table(name = "informational_messages_client")
class InformationalMessageClient(
    /** Уникальный идентификатор сообщения. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,
    /** Тип уведомления. */
    @Column(name = "type", nullable = false)
    var type: String,
    /** Описание уведомления. */
    @Column(name = "description", nullable = false)
    var description: String,
    /** Дата и время начала отображения. */
    @Column(name = "display_with", nullable = false)
    var displayWith: LocalDateTime,
    /** Дата и время окончания отображения. */
    @Column(name = "display_to", nullable = false)
    var displayTo: LocalDateTime,
    /** Признак отображения сообщения. */
    @Column(name = "check_display", nullable = false)
    var checkDisplay: Boolean = true,
    /** Дата/время создания записи. */
    @CreationTimestamp
    @Column(name = "create_date", nullable = false, updatable = false)
    var createDate: Instant? = null,
    /** Дата/время последнего обновления записи. */
    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    var updateDate: Instant? = null,
)
