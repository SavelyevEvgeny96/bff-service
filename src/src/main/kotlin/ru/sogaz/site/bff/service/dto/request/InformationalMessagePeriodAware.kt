package ru.sogaz.site.bff.service.dto.request

import java.time.LocalDateTime

/**
 * Общий контракт DTO, содержащих период отображения информационного сообщения.
 */
interface InformationalMessagePeriodAware {
    val displayWith: LocalDateTime?
    val displayTo: LocalDateTime?
}
