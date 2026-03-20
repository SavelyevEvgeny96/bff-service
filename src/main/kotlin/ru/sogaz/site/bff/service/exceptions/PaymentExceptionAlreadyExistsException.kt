package ru.sogaz.site.bff.service.exceptions

import java.util.UUID

/** Исключение уже существует. */
class PaymentExceptionAlreadyExistsException(
    private val productId: UUID,
    private val paymentType: String,
    cause: Throwable? = null,
) : RuntimeException("Ошибка добавления существующего productId=$productId, paymentType=$paymentType", cause)
