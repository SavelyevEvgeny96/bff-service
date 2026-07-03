package ru.sogaz.site.bff.service.dto.request

import java.time.OffsetDateTime

/**
 * Общий контракт DTO, содержащих период отображения информационного сообщения.
 */
interface InformationalMessagePeriodAware {
    val displayWith: OffsetDateTime?
    val displayTo: OffsetDateTime?
}
