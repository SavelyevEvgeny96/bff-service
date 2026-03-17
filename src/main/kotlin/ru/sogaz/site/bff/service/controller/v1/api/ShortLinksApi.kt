package ru.sogaz.site.bff.service.controller.v1.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.view.RedirectView
import ru.sogaz.siter.models.resonses.Response

/**
 * API для работы с короткими ссылками.
 *
 * Контракт BFF для получения длинной ссылки по shortCode.
 */
@Tag(
    name = "Short Links API",
    description = "API для получения длинной ссылки по короткому коду",
)
@Validated
@RequestMapping("/v1")
interface ShortLinksApi {
    /**
     * Получить длинную ссылку по короткому коду.
     *
     * @param shortCode короткий код ссылки
     */
    @Operation(
        summary = "Получение длинной ссылки",
        description = "Возвращает длинную ссылку по shortCode",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Ссылка успешно получена",
            ),
            ApiResponse(
                responseCode = "422",
                description = "Некорректные параметры запроса",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Response::class),
                    ),
                ],
            ),
            ApiResponse(
                responseCode = "504",
                description = "Ошибка получения ссылки из сервиса коротких ссылок",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = Response::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping("/shortlink")
    fun getShortLinks(
        @Parameter(
            description = "Короткий код ссылки",
            required = true,
            example = "abc123",
        )
        @RequestParam
        @NotBlank(message = "Не заполнено обязательное значение") shortCode: String?,
    ): RedirectView
}
