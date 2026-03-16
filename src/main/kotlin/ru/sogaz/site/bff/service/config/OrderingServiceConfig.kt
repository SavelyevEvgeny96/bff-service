package ru.sogaz.site.bff.service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.sogaz.site.ordering.client.api.PaymentPageInfoControllerApi
import ru.sogaz.site.ordering.client.invoker.ApiClient

@Configuration
class OrderingServiceConfig {
    @Bean
    fun orderingServiceControllerApi(): PaymentPageInfoControllerApi {
        val apiClient = ApiClient()
        apiClient.basePath
        return PaymentPageInfoControllerApi(apiClient)
    }
}