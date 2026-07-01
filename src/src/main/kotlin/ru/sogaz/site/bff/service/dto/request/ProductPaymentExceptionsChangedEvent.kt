package ru.sogaz.site.bff.service.dto.request

import java.util.UUID

/**
 * Событие: изменились исключения по оплатам для конкретного продукта.
 */
class ProductPaymentExceptionsChangedEvent(
    /** Продукт, для которого нужно обновить кэш исключений. */
    val productId: UUID,
)
