package ru.sogaz.site.bff.service.service

import java.net.URI

interface LkAuthorizationService {
    fun createAuthorizationUri(redirectUrl: String): URI
}
