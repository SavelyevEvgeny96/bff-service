package ru.sogaz.site.bff.service.controller


import org.springframework.web.bind.annotation.RestController
import ru.sogaz.site.bff.service.api.ShortLinksApi
import ru.sogaz.site.payment.client.api.ShortLinkControllerApi

/**
 * Реализация API коротких ссылок.
 */
@RestController
class ShortLinksController(
    private val shortLinkControllerApi: ShortLinkControllerApi,
) : ShortLinksApi {

    override fun getShortLinks(shortCode: String) =
        shortLinkControllerApi.redirectToLongUrl(shortCode)
}