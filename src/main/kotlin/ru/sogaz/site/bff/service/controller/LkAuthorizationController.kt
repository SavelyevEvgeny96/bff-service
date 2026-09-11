package ru.sogaz.site.bff.service.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import ru.sogaz.site.bff.service.controller.v1.api.LkAuthorizationApi
import ru.sogaz.site.bff.service.service.LkAuthorizationService

@RestController
class LkAuthorizationController(
    private val service: LkAuthorizationService,
) : LkAuthorizationApi {
    override fun authorize(redirectUrl: String): ResponseEntity<Void> =
        ResponseEntity.status(302)
            .location(service.createAuthorizationUri(redirectUrl))
            .build()
}
