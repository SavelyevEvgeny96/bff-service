package ru.sogaz.site.bff.service.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotNull
import ru.sogaz.site.bff.service.constraint.ValidCertificateVerificationSource

/** Данные результата проверки сертификата пользователя. */
@Schema(description = "Результат проверки наличия сертификата Минцифры")
@ValidCertificateVerificationSource
data class CertificateVerificationRequest(
    @field:Schema(description = "Код из короткой ссылки. Обязателен, если не указан invoiceId", example = "Dpa7mUHB")
    val shortLink: String?,
    @field:Schema(
        description = "Идентификатор счета. Обязателен, если не указан shortLink",
        example = "550e8400-e29b-41d4-a716-446655440000",
        format = "uuid",
    )
    val invoiceId: String?,
    @field:NotNull(message = "Не заполнено обязательное значение")
    @field:Schema(description = "Результат проверки наличия сертификата", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    val certMin: Boolean?,
)
