package ru.sogaz.site.bff.service.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/** Данные результата проверки сертификата пользователя. */
@Schema(description = "Результат проверки наличия сертификата Минцифры")
data class CertificateVerificationRequest(
    @field:NotBlank(message = "Не заполнено обязательное значение")
    @field:Schema(description = "Код из короткой ссылки", example = "Dpa7mUHB", requiredMode = Schema.RequiredMode.REQUIRED)
    val shortLink: String?,
    @field:NotNull(message = "Не заполнено обязательное значение")
    @field:Schema(description = "Результат проверки наличия сертификата", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    val certMin: Boolean?,
)
