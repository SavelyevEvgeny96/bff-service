package ru.sogaz.site.bff.service.repository

import org.springframework.data.jpa.repository.JpaRepository
import ru.sogaz.site.bff.service.model.CertificateVerification
import java.util.UUID

interface CertificateVerificationRepository : JpaRepository<CertificateVerification, UUID>
