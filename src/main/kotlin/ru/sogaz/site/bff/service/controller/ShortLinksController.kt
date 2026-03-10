package ru.sogaz.site.bff.service.controller

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView
import ru.sogaz.site.bff.service.api.ShortLinksApi
import ru.sogaz.site.shortlinks.client.api.ShortLinkControllerApi

/**
 * Реализация API коротких ссылок.
 */
@RestController
class ShortLinksController(
    private val shortLinkControllerApi: ShortLinkControllerApi,
) : ShortLinksApi {
    override fun getShortLinks(shortCode: String): RedirectView {
        val response = shortLinkControllerApi.redirectToLongUrl(shortCode, false)
        val url = (response as Map<*, *>)["url"] as String
        return RedirectView(url)
    }
}
