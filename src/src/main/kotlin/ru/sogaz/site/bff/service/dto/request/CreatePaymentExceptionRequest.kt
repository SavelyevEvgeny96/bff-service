package ru.sogaz.site.bff.service.dto.request

import jakarta.validation.constraints.NotBlank

/**
 * Запрос на создание исключения по способу оплаты для продукта.
 */
data class CreatePaymentExceptionRequest(
    /**
     * Тип способа оплаты (payment_methods.type), например CARD / SBP.
     *
     * <p>Обязательное поле: не null и не пустая строка.</p>
     */
    @field:NotBlank(message = "поле paymentType не должен быть null или пустая строка")
    val paymentType: String,
)
