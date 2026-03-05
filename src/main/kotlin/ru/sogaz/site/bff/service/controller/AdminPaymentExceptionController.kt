package ru.sogaz.site.bff.service.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import ru.sogaz.site.bff.service.dto.request.CreatePaymentExceptionRequest
import ru.sogaz.site.bff.service.dto.response.CreatePaymentExceptionResponse
import ru.sogaz.site.bff.service.service.AdminPaymentExceptionServiceImpl
import java.util.UUID

/**
 * Админские ручки управления исключениями по оплатам.
 */
@RestController
@RequestMapping("/admin")
class AdminPaymentExceptionController(
    private val service: AdminPaymentExceptionServiceImpl,
) {
    /**
     * Создать исключение: запретить оплату продукту указанным paymentType.
     *
     * <p>Кэш excludedByProduct для productId обновляется без рестарта после успешного коммита.</p>
     */
    @PostMapping("/products/{productId}/payment-exceptions")
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable productId: UUID,
        @RequestBody @Valid request: CreatePaymentExceptionRequest,
    ): CreatePaymentExceptionResponse {
        val id = service.createException(productId, request.paymentType.trim())
        return CreatePaymentExceptionResponse(id.toString())
    }
}
