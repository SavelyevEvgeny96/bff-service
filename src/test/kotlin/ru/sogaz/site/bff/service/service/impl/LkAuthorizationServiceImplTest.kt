package ru.sogaz.site.bff.service.service.impl

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.sogaz.site.bff.service.mapper.RedirectUrlAuthMapper
import ru.sogaz.site.bff.service.model.RedirectUrlAuth
import ru.sogaz.site.bff.service.properties.LkAuthorizationProperties
import ru.sogaz.site.bff.service.repository.RedirectUrlAuthRepository
import java.util.UUID
import kotlin.test.assertEquals

class LkAuthorizationServiceImplTest {
    private val repository = mock<RedirectUrlAuthRepository>()
    private val mapper = mock<RedirectUrlAuthMapper>()
    private val properties = LkAuthorizationProperties(
        url = "https://lk-stage.sogaz.ru/auth/oauth2",
        clientId = "payment-service",
        responseType = "code",
    )
    private val service = LkAuthorizationServiceImpl(repository, mapper, properties)

    @Test
    fun `saves redirect url and creates LK authorization URI`() {
        val id = UUID.fromString("2b57a70b-8abc-491d-9b13-654c77c12f16")
        val url = "https://test.pay.sogaz.ru/payment?id=42"
        val entity = RedirectUrlAuth(url = url)
        whenever(mapper.toEntity(url)).thenReturn(entity)
        whenever(repository.saveAndFlush(entity)).thenReturn(entity.apply { this.id = id })

        val result = assertDoesNotThrow { service.createAuthorizationUri(url) }

        assertEquals(
            "https://lk-stage.sogaz.ru/auth/oauth2?client_id=payment-service&response_type=code&state=$id",
            result.toString(),
        )
        verify(mapper).toEntity(url)
        verify(repository).saveAndFlush(entity)
    }
}
