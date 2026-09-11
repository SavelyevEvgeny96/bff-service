package ru.sogaz.site.bff.service.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import java.time.Instant
import java.util.UUID

/** Факт проверки наличия сертификата перед переходом к инструкции. */
@Entity
@Table(name = "certificate_verification")
class CertificateVerification(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    var id: UUID? = null,
    @Column(name = "invoice_id", nullable = false)
    var invoiceId: UUID,
    @Column(name = "cert_min", nullable = false)
    var certMin: Boolean,
    @CreationTimestamp
    @Column(name = "date_verification", nullable = false, updatable = false)
    var dateVerification: Instant? = null,
)
