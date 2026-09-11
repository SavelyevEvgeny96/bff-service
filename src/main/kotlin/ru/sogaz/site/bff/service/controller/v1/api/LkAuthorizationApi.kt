package ru.sogaz.site.bff.service.controller.v1.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Tag(name = "LK Authorization API", description = "Переход на страницу авторизации личного кабинета")
@Validated
interface LkAuthorizationApi {
    @Operation(summary = "Формирование ссылки для перехода на страницу авторизации ЛК")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "302", description = "Перенаправление на страницу авторизации ЛК"),
            ApiResponse(responseCode = "400", description = "Не передан адрес возврата клиента"),
        ],
    )
    @GetMapping("/login/oauth/authorize")
    fun authorize(@RequestParam("p") @NotBlank redirectUrl: String): ResponseEntity<Void>
}
