package ru.sogaz.site.bff.service.dto.request

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import ru.sogaz.site.bff.service.constraint.ValidInformationalMessagePeriod
import ru.sogaz.site.bff.service.constraint.ValidInformationalMessageType
import java.time.OffsetDateTime

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
    /** Дата и время начала отображения в формате ISO 8601. */
    @field:NotNull(message = "{informational.message.display-with.required}")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    override val displayWith: OffsetDateTime?,
    /** Дата и время окончания отображения в формате ISO 8601. */
    @field:NotNull(message = "{informational.message.display-to.required}")
    @field:JsonFormat(shape = JsonFormat.Shape.STRING)
    override val displayTo: OffsetDateTime?,
) : InformationalMessagePeriodAware
