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
import java.util.UUID

@Entity
@Table(name = "redirect_url_auth")
class RedirectUrlAuth(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,
    @Column(name = "url", nullable = false)
    var url: String,
    @CreationTimestamp
    @Column(name = "create_date", nullable = false, updatable = false)
    var createDate: Instant? = null,
    @UpdateTimestamp
    @Column(name = "update_date", nullable = false)
    var updateDate: Instant? = null,
)
