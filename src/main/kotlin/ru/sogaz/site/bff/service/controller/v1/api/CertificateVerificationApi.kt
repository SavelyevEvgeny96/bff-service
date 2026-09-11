package ru.sogaz.site.bff.service.controller.v1.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import ru.sogaz.site.bff.service.dto.request.CertificateVerificationRequest
import ru.sogaz.siter.models.resonses.Response

@Tag(name = "Certificate Verification API", description = "API сохранения результатов проверки сертификатов")
@Validated
@RequestMapping("/v1")
interface CertificateVerificationApi {
    @Operation(
        summary = "Сохранение результата проверки сертификата",
        description = "Получает счет по короткой ссылке и сохраняет результат проверки сертификата Минцифры",
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "Результат проверки сохранен", content = [Content()]),
            ApiResponse(
                responseCode = "422",
                description = "Некорректные параметры запроса",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Response::class))],
            ),
            ApiResponse(
                responseCode = "500",
                description = "Ошибка обработки или сохранения результата проверки",
                content = [Content(mediaType = "application/json", schema = Schema(implementation = Response::class))],
            ),
        ],
    )
    @PostMapping("/verification")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveVerification(@Valid @RequestBody request: CertificateVerificationRequest)
}
