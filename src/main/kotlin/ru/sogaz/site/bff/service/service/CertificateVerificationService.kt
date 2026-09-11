package ru.sogaz.site.bff.service.service

import ru.sogaz.site.bff.service.dto.request.CertificateVerificationRequest

interface CertificateVerificationService {
    fun save(request: CertificateVerificationRequest)
}
