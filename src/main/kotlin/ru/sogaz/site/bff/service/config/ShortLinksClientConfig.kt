package ru.sogaz.site.bff.service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.sogaz.site.payment.client.api.ShortLinkControllerApi
import ru.sogaz.site.payment.client.invoker.ApiClient

@Configuration
class ShortLinksClientConfig {
    @Bean
    fun shortLinkControllerApi(): ShortLinkControllerApi {
        val apiClient = ApiClient()
        apiClient.basePath
        return ShortLinkControllerApi(apiClient)
    }
}
