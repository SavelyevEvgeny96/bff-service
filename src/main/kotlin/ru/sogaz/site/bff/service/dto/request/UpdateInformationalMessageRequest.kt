package ru.sogaz.site.bff.service.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import ru.sogaz.site.bff.service.constraint.ValidInformationalMessagePeriod
import ru.sogaz.site.bff.service.constraint.ValidInformationalMessageType
import ru.sogaz.site.bff.service.constraint.ValidOptionalNotBlank
import java.time.LocalDateTime

/**
 * Запрос на изменение информационного сообщения для клиента.
 */
@ValidInformationalMessagePeriod
data class UpdateInformationalMessageRequest(
    /** Тип уведомления. */
    @field:ValidOptionalNotBlank
    @field:ValidInformationalMessageType
    val type: String?,
    /** Описание уведомления. */
    @field:ValidOptionalNotBlank
    val description: String?,
    /** Дата и время начала отображения в формате yyyy-MM-dd HH:mm:ss.SSSSS. */
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    override val displayWith: LocalDateTime?,
    /** Дата и время окончания отображения в формате yyyy-MM-dd HH:mm:ss.SSSSS. */
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    override val displayTo: LocalDateTime?,
    /** Признак отображения сообщения. */
    val checkDisplay: Boolean?,
) : InformationalMessagePeriodAware
