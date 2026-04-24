package ru.sogaz.site.bff.service.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "api.ordering")
class OrderingApiProperties(
    val basePath: String,
    val paySuffix: String,
)
