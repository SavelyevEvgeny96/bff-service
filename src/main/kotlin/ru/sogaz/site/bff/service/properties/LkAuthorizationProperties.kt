package ru.sogaz.site.bff.service.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "api.lk-authorization")
class LkAuthorizationProperties(
    val url: String,
    val clientId: String,
    val responseType: String,
)
