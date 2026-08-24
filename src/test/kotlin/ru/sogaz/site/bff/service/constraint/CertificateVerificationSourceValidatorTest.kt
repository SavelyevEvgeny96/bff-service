package ru.sogaz.site.bff.service.constraint

import jakarta.validation.Validation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.sogaz.site.bff.service.dto.request.CertificateVerificationRequest
import java.util.UUID

class CertificateVerificationSourceValidatorTest {
    private val validator = Validation.buildDefaultValidatorFactory().validator

    @Test
    fun `requires short link or invoice id`() {
        val request = CertificateVerificationRequest(shortLink = null, invoiceId = null, certMin = true)

        val violations = validator.validate(request)

        assertEquals(setOf("shortLink", "invoiceId"), violations.map { it.propertyPath.toString() }.toSet())
    }

    @Test
    fun `accepts request with only short link`() {
        val request = CertificateVerificationRequest(shortLink = "Dpa7mUHB", invoiceId = null, certMin = true)

        assertTrue(validator.validate(request).isEmpty())
    }

    @Test
    fun `accepts request with only invoice id`() {
        val request = CertificateVerificationRequest(shortLink = null, invoiceId = UUID.randomUUID().toString(), certMin = true)

        assertTrue(validator.validate(request).isEmpty())
    }

    @Test
    fun `rejects malformed invoice id`() {
        val request = CertificateVerificationRequest(shortLink = null, invoiceId = "not-a-uuid", certMin = true)

        val violations = validator.validate(request)

        assertEquals(listOf("invoiceId"), violations.map { it.propertyPath.toString() })
    }
}
