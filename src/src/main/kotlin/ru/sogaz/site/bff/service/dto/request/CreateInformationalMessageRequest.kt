package ru.sogaz.site.bff.service.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import ru.sogaz.site.bff.service.constraint.ValidInformationalMessagePeriod
import ru.sogaz.site.bff.service.constraint.ValidInformationalMessageType
import java.time.LocalDateTime

/**
 * Запрос на создание информационного сообщения для клиента.
 */
@ValidInformationalMessagePeriod
data class CreateInformationalMessageRequest(
    /** Тип уведомления. */
    @field:NotBlank(message = "{informational.message.type.required}")
    @field:ValidInformationalMessageType
    val type: String?,

    /** Описание уведомления. */
    @field:NotBlank(message = "{informational.message.description.required}")
    val description: String?,

    /** Дата и время начала отображения в формате yyyy-MM-dd HH:mm:ss.SSSSS. */
    @field:NotNull(message = "{informational.message.display-with.required}")
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    val displayWith: LocalDateTime?,

    /** Дата и время окончания отображения в формате yyyy-MM-dd HH:mm:ss.SSSSS. */
    @field:NotNull(message = "{informational.message.display-to.required}")
    @field:JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSSS")
    val displayTo: LocalDateTime?,
) : InformationalMessagePeriodAware
