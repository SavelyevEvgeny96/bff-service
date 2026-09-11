package ru.sogaz.site.bff.service.service.impl

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.util.UriComponentsBuilder
import ru.sogaz.site.bff.service.mapper.RedirectUrlAuthMapper
import ru.sogaz.site.bff.service.properties.LkAuthorizationProperties
import ru.sogaz.site.bff.service.repository.RedirectUrlAuthRepository
import ru.sogaz.site.bff.service.service.LkAuthorizationService
import java.net.URI

@Service
class LkAuthorizationServiceImpl(
    private val repository: RedirectUrlAuthRepository,
    private val mapper: RedirectUrlAuthMapper,
    private val properties: LkAuthorizationProperties,
) : LkAuthorizationService {
    @Transactional
    override fun createAuthorizationUri(redirectUrl: String): URI {
        val redirect = repository.saveAndFlush(mapper.toEntity(redirectUrl))

        return UriComponentsBuilder.fromUriString(properties.url)
            .queryParam("client_id", properties.clientId)
            .queryParam("response_type", properties.responseType)
            .queryParam("state", requireNotNull(redirect.id))
            .encode()
            .build()
            .toUri()
    }
}
