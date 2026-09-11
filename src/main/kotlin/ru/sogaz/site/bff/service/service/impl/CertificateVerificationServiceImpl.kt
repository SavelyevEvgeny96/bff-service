package ru.sogaz.site.bff.service.service.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.sogaz.site.bff.service.dto.request.CertificateVerificationRequest
import ru.sogaz.site.bff.service.mapper.CertificateVerificationMapper
import ru.sogaz.site.bff.service.repository.CertificateVerificationRepository
import ru.sogaz.site.bff.service.service.CertificateVerificationService
import ru.sogaz.site.exceptionStarter.starter.dto.exceptions.InnerException
import ru.sogaz.site.filterStarter.services.RequestInfo
import ru.sogaz.site.shortlinks.client.api.ShortLinkControllerApi
import java.net.URI
import java.util.UUID

@Service
class CertificateVerificationServiceImpl(
    private val shortLinkControllerApi: ShortLinkControllerApi,
    private val repository: CertificateVerificationRepository,
    private val mapper: CertificateVerificationMapper,
) : CertificateVerificationService {
    companion object {
        const val INVALID_SHORT_LINK_RESPONSE = "Сервис коротких ссылок вернул некорректную ссылку"
        private const val PAYMENT_PATH_SEGMENT = "p"
    }

    @Transactional
    override fun save(request: CertificateVerificationRequest) {
        val shortLink = requireNotNull(request.shortLink)
        val response = shortLinkControllerApi.redirectToLongUrl(shortLink, false)
        val url = (response as? Map<*, *>)?.get("url") as? String
        val invoiceId = url?.extractInvoiceId() ?: invalidShortLinkResponse()

        repository.saveAndFlush(mapper.toEntity(request, invoiceId))
    }

    private fun String.extractInvoiceId(): UUID? =
        runCatching {
            val segments = URI.create(this).path.split('/').filter(String::isNotBlank)
            val paymentSegmentIndex = segments.indexOfLast { it == PAYMENT_PATH_SEGMENT }
            require(paymentSegmentIndex >= 0)
            UUID.fromString(segments.getOrNull(paymentSegmentIndex + 1))
        }.getOrNull()

    private fun invalidShortLinkResponse(): Nothing =
        throw InnerException(RequestInfo.getTraceId(), INVALID_SHORT_LINK_RESPONSE)
}
