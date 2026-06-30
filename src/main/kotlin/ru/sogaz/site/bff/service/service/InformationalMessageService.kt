package ru.sogaz.site.bff.service.service

import ru.sogaz.site.bff.service.dto.request.CreateInformationalMessageRequest
import ru.sogaz.site.bff.service.dto.request.UpdateInformationalMessageRequest
import ru.sogaz.site.bff.service.dto.response.InformationalMessageResponse
import java.util.UUID

interface InformationalMessageService {
    fun create(request: CreateInformationalMessageRequest): UUID

    fun update(
        messageId: UUID,
        request: UpdateInformationalMessageRequest,
    ): UUID

    fun getMessages(checkDisplay: Boolean?): List<InformationalMessageResponse>
}
