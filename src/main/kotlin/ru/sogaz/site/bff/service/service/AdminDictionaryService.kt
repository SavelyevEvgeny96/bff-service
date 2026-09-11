package ru.sogaz.site.bff.service.service

import ru.sogaz.site.bff.service.dto.request.AdminAddRequest
import ru.sogaz.site.bff.service.dto.response.ItemResponse

interface AdminDictionaryService {
    fun add(request: AdminAddRequest): List<ItemResponse?>
}
