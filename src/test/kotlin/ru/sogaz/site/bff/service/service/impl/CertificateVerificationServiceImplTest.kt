package ru.sogaz.site.bff.service.service.impl

import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.sogaz.site.bff.service.dto.request.CertificateVerificationRequest
import ru.sogaz.site.bff.service.mapper.CertificateVerificationMapper
import ru.sogaz.site.bff.service.model.CertificateVerification
import ru.sogaz.site.bff.service.repository.CertificateVerificationRepository
import ru.sogaz.site.exceptionStarter.starter.dto.exceptions.InnerException
import ru.sogaz.site.shortlinks.client.api.ShortLinkControllerApi
import java.util.UUID

class CertificateVerificationServiceImplTest {
    private val shortLinksApi = mock<ShortLinkControllerApi>()
    private val repository = mock<CertificateVerificationRepository>()
    private val mapper = mock<CertificateVerificationMapper>()
    private val service = CertificateVerificationServiceImpl(shortLinksApi, repository, mapper)

    @Test
    fun `saves verification using invoice id from long link`() {
        val invoiceId = UUID.randomUUID()
        val request = CertificateVerificationRequest(shortLink = "Dpa7mUHB", certMin = true)
        val entity = CertificateVerification(invoiceId = invoiceId, certMin = true)
        whenever(shortLinksApi.redirectToLongUrl("Dpa7mUHB", false))
            .thenReturn(mapOf("url" to "https://example.sogaz.ru/p/$invoiceId?source=short"))
        whenever(mapper.toEntity(request, invoiceId)).thenReturn(entity)

        service.save(request)

        verify(shortLinksApi).redirectToLongUrl("Dpa7mUHB", false)
        verify(mapper).toEntity(request, invoiceId)
        verify(repository).saveAndFlush(entity)
    }

    @Test
    fun `does not save when long link has no payment invoice id`() {
        val request = CertificateVerificationRequest(shortLink = "invalid", certMin = false)
        whenever(shortLinksApi.redirectToLongUrl("invalid", false))
            .thenReturn(mapOf("url" to "https://example.sogaz.ru/instructions"))

        assertThrows(InnerException::class.java) { service.save(request) }

        verify(mapper, never()).toEntity(any(), any())
        verify(repository, never()).saveAndFlush(any())
    }
}
