package ru.sogaz.site.bff.service.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.sogaz.site.bff.service.properties.OrderingApiProperties
import ru.sogaz.site.ordering.client.api.InvoicePayPageInfoControllerApi
import ru.sogaz.site.ordering.client.invoker.ApiClient

@Configuration
class OrderingServiceConfig(
    private val orderingApiProperties: OrderingApiProperties,
) {
    @Bean
    fun invoicePayPageInfoControllerApi(): InvoicePayPageInfoControllerApi =
        ApiClient()
            .apply { basePath = orderingApiProperties.basePath }
            .run(::InvoicePayPageInfoControllerApi)
}
