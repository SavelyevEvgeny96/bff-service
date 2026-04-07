package ru.sogaz.site.bff.service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.sogaz.site.bff.service.properties.OrderingApiProperties
import ru.sogaz.site.ordering.client.api.PaymentPageInfoControllerApi
import ru.sogaz.site.ordering.client.invoker.ApiClient

@Configuration
class OrderingServiceConfig(
    private val orderingApiProperties: OrderingApiProperties,
) {
    @Bean
    fun orderingServiceControllerApi(): PaymentPageInfoControllerApi =
        ApiClient()
            .apply { basePath = orderingApiProperties.basePath }
            .run(::PaymentPageInfoControllerApi)
}
