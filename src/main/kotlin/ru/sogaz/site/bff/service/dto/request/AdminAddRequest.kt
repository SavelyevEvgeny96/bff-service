package ru.sogaz.site.bff.service.dto.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty

/**
 * Тело запроса на добавление новых продуктов и способов оплаты.
 *
 * @property list список элементов для добавления.
 */
data class AdminAddRequest(
    /**
     * Список элементов для добавления.
     */
    @field:NotEmpty(message = "Список list не должен быть пустым")
    @field:Valid
    val list: List<AdminAddItemRequest>,
)
