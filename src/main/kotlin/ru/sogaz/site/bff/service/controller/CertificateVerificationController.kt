package ru.sogaz.site.bff.service.controller

import org.springframework.web.bind.annotation.RestController
import ru.sogaz.site.bff.service.controller.v1.api.CertificateVerificationApi
import ru.sogaz.site.bff.service.dto.request.CertificateVerificationRequest
import ru.sogaz.site.bff.service.service.CertificateVerificationService

@RestController
class CertificateVerificationController(
    private val service: CertificateVerificationService,
) : CertificateVerificationApi {
    override fun saveVerification(request: CertificateVerificationRequest) = service.save(request)
}
