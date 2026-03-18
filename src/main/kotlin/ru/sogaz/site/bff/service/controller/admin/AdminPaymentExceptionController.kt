package ru.sogaz.site.bff.service.controller.admin

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
import ru.sogaz.site.filterStarter.services.RequestInfo.getTraceId
import ru.sogaz.siter.models.resonses.Response
import ru.sogaz.siter.models.resonses.getSuccessResponse
import java.util.UUID

/**
 * Админские ручки управления исключениями по оплатам.
 */
@RestController
@RequestMapping("/admin")
class AdminPaymentExceptionController(
    private val service: AdminPaymentExceptionServiceImpl,
) {
    companion object {
        const val CODE_SUCCESS = 1101543201
    }

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
    ): Response<CreatePaymentExceptionResponse> {
        val id = service.createException(productId, request.paymentType.trim())
        return getSuccessResponse(
            getTraceId(),
            CODE_SUCCESS,
            CreatePaymentExceptionResponse(id.toString())
        )
    }
}
